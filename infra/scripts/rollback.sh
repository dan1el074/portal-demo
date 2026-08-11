#!/bin/bash

set -Eeuo pipefail

CONFIG_FILE="/etc/portal-deploy.conf"

[[ -r "$CONFIG_FILE" ]] || {
    echo "Configuração ausente: $CONFIG_FILE"
    exit 1
}

source "$CONFIG_FILE"

REQUIRED_VARIABLES=(
    ENVIRONMENT
    PROJECT_DIR
    APP_DIR
    NGINX_DIR
    DATABASE_NAME
    APP_SERVICE
    NGINX_SERVICE
    BACKUP_ROOT
    LOG_ROOT
    BACKEND_HEALTH_URL
    BACKEND_HEALTH_TIMEOUT
    BACKEND_HEALTH_INTERVAL
    FRONTEND_HEALTH_URL
)

for VARIABLE in "${REQUIRED_VARIABLES[@]}"; do
    [[ -n "${!VARIABLE:-}" ]] || {
        echo "Variável ausente: $VARIABLE"
        exit 1
    }
done

[[ "$BACKEND_HEALTH_TIMEOUT" =~ ^[1-9][0-9]*$ ]] || {
    echo "BACKEND_HEALTH_TIMEOUT inválido"
    exit 1
}

[[ "$BACKEND_HEALTH_INTERVAL" =~ ^[1-9][0-9]*$ ]] || {
    echo "BACKEND_HEALTH_INTERVAL inválido"
    exit 1
}

[[ "$BACKUP_ROOT" == "/home/metaro/backup" ]] || {
    echo "Diretório de backups não permitido: $BACKUP_ROOT"
    exit 1
}

[[ "$NGINX_DIR" == "/var/www/app" ]] || {
    echo "Diretório do frontend não permitido: $NGINX_DIR"
    exit 1
}

[[ "$DATABASE_NAME" =~ ^[A-Za-z0-9_]+$ ]] || {
    echo "Nome de banco inválido"
    exit 1
}

PROD_CONFIG="$PROJECT_DIR/backend/src/main/resources/application-prod.properties"

list_backups() {
    echo "Backups disponíveis em $BACKUP_ROOT:"
    echo

    FOUND=false

    while IFS= read -r BACKUP_NAME; do
        BACKUP_PATH="$BACKUP_ROOT/$BACKUP_NAME"

        [[ -f "$BACKUP_PATH/READY" ]] || continue

        FOUND=true

        if [[ -f "$BACKUP_PATH/DEPLOY_SUCCESS" ]]; then
            STATUS="deploy concluído"
        else
            STATUS="backup de segurança"
        fi

        SIZE="$(du -sh "$BACKUP_PATH" | cut -f1)"

        printf '%s | %s | %s\n' \
            "$BACKUP_NAME" \
            "$SIZE" \
            "$STATUS"
    done < <(
        find "$BACKUP_ROOT" \
            -mindepth 1 \
            -maxdepth 1 \
            -type d \
            -name "????????-??????" \
            -printf "%f\n" |
        sort -r
    )

    [[ "$FOUND" == "true" ]] ||
        echo "Nenhum backup compatível encontrado."
}

if [[ "${1:-}" == "--list" ]]; then
    list_backups
    exit 0
fi

if [[ $# -lt 1 || $# -gt 2 ]]; then
    echo "Uso:"
    echo "  rollback.sh --list"
    echo "  rollback.sh DATA_HORA"
    echo "  rollback.sh DATA_HORA --with-database"
    exit 1
fi

BACKUP_NAME="$1"
RESTORE_DATABASE=false

if [[ "${2:-}" == "--with-database" ]]; then
    RESTORE_DATABASE=true
elif [[ -n "${2:-}" ]]; then
    echo "Opção inválida: $2"
    exit 1
fi

[[ "$BACKUP_NAME" =~ ^[0-9]{8}-[0-9]{6}$ ]] || {
    echo "Identificador de backup inválido"
    exit 1
}

BACKUP_PATH="$BACKUP_ROOT/$BACKUP_NAME"

[[ "$BACKUP_PATH" == "$BACKUP_ROOT/"* ]] || {
    echo "Caminho de backup inválido"
    exit 1
}

REQUIRED_BACKUP_FILES=(
    "$BACKUP_PATH/READY"
    "$BACKUP_PATH/app.jar"
    "$BACKUP_PATH/application-prod.properties"
    "$BACKUP_PATH/database.dump"
)

for BACKUP_FILE in "${REQUIRED_BACKUP_FILES[@]}"; do
    [[ -f "$BACKUP_FILE" ]] || {
        echo "Backup incompleto: $BACKUP_FILE"
        exit 1
    }
done

[[ -d "$BACKUP_PATH/frontend" ]] || {
    echo "Frontend ausente no backup"
    exit 1
}

[[ -s "$PROD_CONFIG" ]] || {
    echo "application-prod.properties atual está ausente"
    exit 1
}

[[ -f "$APP_DIR/app.jar" ]] || {
    echo "JAR atual está ausente"
    exit 1
}

[[ -d "$NGINX_DIR" ]] || {
    echo "Frontend atual está ausente"
    exit 1
}

TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
SAFETY_BACKUP="$BACKUP_ROOT/$TIMESTAMP"
LOG_FILE="$LOG_ROOT/rollback-$TIMESTAMP.log"

RESTORED_FRONTEND="$NGINX_DIR.rollback-$TIMESTAMP"
OLD_FRONTEND="$NGINX_DIR.before-rollback-$TIMESTAMP"

APP_STOPPED=false
RESTORE_STARTED=false

[[ ! -e "$SAFETY_BACKUP" ]] || {
    echo "Já existe um backup com o identificador $TIMESTAMP"
    exit 1
}

exec 9>"$LOG_ROOT/portal-operation.lock"

flock -n 9 || {
    echo "Já existe um deploy ou rollback em execução"
    exit 1
}

exec > >(tee -a "$LOG_FILE") 2>&1

rollback_message() {
    echo
    echo -e "[\e[32mROLLBACK\e[0m] $1"
}

abort_rollback() {
    EXIT_CODE="${2:-1}"
    trap - ERR

    echo
    echo -e "[\e[31mERRO\e[0m] $1"

    if [[ "$APP_STOPPED" == "true" &&
          "$RESTORE_STARTED" == "false" ]]; then
        echo "Reiniciando o backend original..."
        sudo systemctl start "$APP_SERVICE" || true
    fi

    if [[ -d "$SAFETY_BACKUP" ]]; then
        echo "Backup de segurança atual: $TIMESTAMP"
    fi

    journalctl -u "$APP_SERVICE" -n 80 --no-pager || true

    echo "Log: $LOG_FILE"
    exit "$EXIT_CODE"
}

trap 'abort_rollback "Falha inesperada na linha $LINENO" "$?"' ERR

rollback_message "Ambiente: $ENVIRONMENT"
rollback_message "Backup selecionado: $BACKUP_NAME"

if [[ -f "$BACKUP_PATH/manifest.txt" ]]; then
    echo
    cat "$BACKUP_PATH/manifest.txt"
fi

echo
echo "Serão restaurados:"
echo "- backend"
echo "- frontend"
echo "- application-prod.properties"

if [[ "$RESTORE_DATABASE" == "true" ]]; then
    echo "- banco PostgreSQL"
    echo
    echo "ATENÇÃO: dados criados após o backup serão perdidos."
    EXPECTED_CONFIRMATION="ROLLBACK DATABASE $BACKUP_NAME"
else
    echo "- banco PostgreSQL: NÃO será restaurado"
    EXPECTED_CONFIRMATION="ROLLBACK $BACKUP_NAME"
fi

echo
read -r -p \
    "Digite '$EXPECTED_CONFIRMATION' para continuar: " \
    CONFIRMATION

[[ "$CONFIRMATION" == "$EXPECTED_CONFIRMATION" ]] || {
    echo "Rollback cancelado."
    exit 1
}

sudo -v

SERVICE_ENVIRONMENT="$(
    systemctl show "$APP_SERVICE" -p Environment --value
)"

grep -qw "APP_PROFILE=prod" <<< "$SERVICE_ENVIRONMENT" ||
    abort_rollback "APP_PROFILE=prod não está configurado"

grep -qw \
    "FLYWAY_BASELINE_ON_MIGRATE=false" \
    <<< "$SERVICE_ENVIRONMENT" ||
    abort_rollback "O baseline do Flyway precisa estar desativado"

rollback_message "Preparando frontend restaurado"

sudo cp -a \
    "$BACKUP_PATH/frontend" \
    "$RESTORED_FRONTEND"

sudo chown -R \
    www-data:www-data \
    "$RESTORED_FRONTEND"

rollback_message "Criando backup de segurança do estado atual"

mkdir -p "$SAFETY_BACKUP"
chmod 700 "$SAFETY_BACKUP"

sudo install \
    -o metaro \
    -g metaro \
    -m 600 \
    "$APP_DIR/app.jar" \
    "$SAFETY_BACKUP/app.jar"

cp "$PROD_CONFIG" \
   "$SAFETY_BACKUP/application-prod.properties"

chmod 600 \
    "$SAFETY_BACKUP/application-prod.properties"

sudo cp -a \
    "$NGINX_DIR" \
    "$SAFETY_BACKUP/frontend"

sudo systemctl stop "$APP_SERVICE"
APP_STOPPED=true

sudo -u postgres pg_dump \
    --format=custom \
    --create \
    "$DATABASE_NAME" \
    > "$SAFETY_BACKUP/database.dump"

chmod 600 "$SAFETY_BACKUP/database.dump"

printf '%s\n' \
    "timestamp=$TIMESTAMP" \
    "environment=$ENVIRONMENT" \
    "type=pre-rollback-safety" \
    "rollback_target=$BACKUP_NAME" \
    > "$SAFETY_BACKUP/manifest.txt"

touch "$SAFETY_BACKUP/READY"

RESTORE_STARTED=true

if [[ "$RESTORE_DATABASE" == "true" ]]; then
    rollback_message "Restaurando PostgreSQL"

    sudo -u postgres psql \
        -d postgres \
        -v ON_ERROR_STOP=1 \
        -c "SELECT pg_terminate_backend(pid)
            FROM pg_stat_activity
            WHERE datname = '$DATABASE_NAME'
              AND pid <> pg_backend_pid();"

    sudo -u postgres dropdb \
        --if-exists \
        --force \
        "$DATABASE_NAME"

    sudo -u postgres pg_restore \
        --exit-on-error \
        --create \
        --dbname=postgres \
        < "$BACKUP_PATH/database.dump"
fi

rollback_message "Restaurando backend e configuração"

sudo install \
    -o metaro \
    -g metaro \
    -m 640 \
    "$BACKUP_PATH/app.jar" \
    "$APP_DIR/app.jar"

sudo install \
    -o metaro \
    -g metaro \
    -m 600 \
    "$BACKUP_PATH/application-prod.properties" \
    "$PROD_CONFIG"

rollback_message "Restaurando frontend"

sudo mv \
    "$NGINX_DIR" \
    "$OLD_FRONTEND"

sudo mv \
    "$RESTORED_FRONTEND" \
    "$NGINX_DIR"

sudo systemctl start "$APP_SERVICE"
APP_STOPPED=false

BACKEND_READY=false
BACKEND_HEALTH_DEADLINE=$((SECONDS + BACKEND_HEALTH_TIMEOUT))
BACKEND_HEALTH_NEXT_STATUS=$((SECONDS + 30))

rollback_message "Aguardando o backend por até ${BACKEND_HEALTH_TIMEOUT}s"

while (( SECONDS < BACKEND_HEALTH_DEADLINE )); do
    if systemctl is-active --quiet "$APP_SERVICE" &&
       curl --fail --silent --output /dev/null \
           --connect-timeout 1 --max-time 2 \
           "$BACKEND_HEALTH_URL"; then
        BACKEND_READY=true
        break
    fi

    if (( SECONDS >= BACKEND_HEALTH_NEXT_STATUS )); then
        echo "Backend ainda está inicializando ($((BACKEND_HEALTH_TIMEOUT - BACKEND_HEALTH_DEADLINE + SECONDS))s decorridos)..."
        BACKEND_HEALTH_NEXT_STATUS=$((BACKEND_HEALTH_NEXT_STATUS + 30))
    fi

    BACKEND_HEALTH_REMAINING=$((BACKEND_HEALTH_DEADLINE - SECONDS))
    (( BACKEND_HEALTH_REMAINING > 0 )) || break
    (( BACKEND_HEALTH_REMAINING < BACKEND_HEALTH_INTERVAL )) &&
        sleep "$BACKEND_HEALTH_REMAINING" ||
        sleep "$BACKEND_HEALTH_INTERVAL"
done

[[ "$BACKEND_READY" == "true" ]] ||
    abort_rollback "O backend restaurado não iniciou"

sudo nginx -t

if systemctl is-active --quiet "$NGINX_SERVICE"; then
    sudo systemctl reload "$NGINX_SERVICE"
else
    sudo systemctl start "$NGINX_SERVICE"
fi

curl --fail \
    --silent \
    --location \
    --output /dev/null \
    "$FRONTEND_HEALTH_URL"

[[ "$OLD_FRONTEND" == "$NGINX_DIR.before-rollback-$TIMESTAMP" ]] ||
    abort_rollback "Diretório temporário inesperado"

sudo rm -rf -- "$OLD_FRONTEND"

touch "$SAFETY_BACKUP/ROLLBACK_SUCCESS"

trap - ERR

rollback_message "Concluído com sucesso"

echo "Origem restaurada: $BACKUP_NAME"
echo "Backup de segurança: $TIMESTAMP"
echo "Banco restaurado: $RESTORE_DATABASE"
echo "Log: $LOG_FILE"

systemctl is-active "$APP_SERVICE"
systemctl is-active "$NGINX_SERVICE"

sudo -u postgres psql \
    -d "$DATABASE_NAME" \
    -c "SELECT version, description, success
        FROM flyway_schema_history
        ORDER BY installed_rank;"
