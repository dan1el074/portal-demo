# Portal Metaro para Android

Aplicativo Android nativo que exibe `http://portal.metaro.com.br` em uma WebView, sem barra de endereço.

## Comportamento configurado

- Mantém visíveis as barras de status/notificações e de navegação do Android.
- Mantém dentro do app somente URLs de `portal.metaro.com.br`.
- Abre links externos fora do aplicativo.
- Integra o botão/gesto Voltar ao histórico do portal.
- Exibe uma tela de erro quando não há conexão.
- Oferece seletor de arquivos para campos de upload.
- Prepara PDFs no cache privado, sem item em Downloads, e chama o seletor Android **Abrir PDF com**.
- Abre endereços internos `/images/{id}` no seletor de aplicativos Android, sem navegar para outra aba.
- Identifica o tipo real de anexos pelo nome e pelo `Content-Type`, abrindo PDF, imagem, planilha, documento e apresentação no aplicativo adequado.
- Converte extensões HTML (`.png`, `.jpeg`, `.jpg`, `.webp`) em MIME types aceitos pelo Android.
- Abre a câmera do sistema para inputs HTML com `capture="environment"`, sem exigir acesso amplo à câmera ou aos arquivos.
- Não ignora erros de certificado HTTPS.
- Não solicita acesso amplo ao armazenamento; os PDFs ficam na área privada externa do app.

## Atenção: URL HTTP

A URL fornecida usa HTTP. Isso significa que usuário, senha, token e conteúdo podem trafegar sem criptografia. A exceção de tráfego não criptografado foi limitada a `portal.metaro.com.br` em `network_security_config.xml`, mas a recomendação é instalar HTTPS antes da distribuição.

Quando HTTPS estiver disponível:

1. Troque `HOME_URL` para `https://portal.metaro.com.br` em `MainActivity.kt`.
2. Remova o `domain-config` que libera HTTP em `network_security_config.xml`.

## Integração dos PDFs existentes

O Angular atual cria uma URL `blob:` e usa `window.open`. Outros aplicativos Android não conseguem receber diretamente esse `blob:`. Por isso o app oferece a ponte JavaScript `PortalMetaroAndroid.openPdf(url, token, fileName)`.

Use o arquivo completo `angular-pdf-integration/file.service.updated.ts` ou o exemplo comentado `file.service.example.ts` para adaptar o serviço. No navegador comum, o comportamento atual é preservado; dentro do aplicativo, o Android baixa o arquivo com o token e abre o seletor de leitores de PDF.

Para anexos genéricos, o Angular usa `PortalMetaroAndroid.openFile(url, token, fileName)`. A função antiga `openPdf` permanece disponível e também deixa de forçar PDF quando o nome recebido possui outra extensão, garantindo compatibilidade com versões anteriores do frontend.

Antes de liberar, confirme o endereço real da API em produção. O arquivo `environment.prod.ts` analisado ainda contém `http://base.url:1234`. Se a API usar HTTP em outro domínio, será necessário acrescentar esse domínio ao `network_security_config.xml`.

## Abrir e gerar APK

As imagens abertas pelo portal são baixadas usando os cookies da sessão atual, ficam somente no cache privado e são removidas automaticamente após 24 horas, assim como os PDFs.

Pré-requisitos:

- Android Studio atual.
- Android SDK 36 instalado.
- JDK 17 ou superior configurado no Android Studio.

Passos:

1. Abra a pasta `portal-metaro-android` no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Conecte um aparelho com depuração USB e execute o app.
4. Para um APK de homologação, use **Build > Build APK(s)**.
5. Para distribuição interna, gere um APK assinado em **Build > Generate Signed App Bundle or APK > APK**.

Não salve a senha do certificado no repositório. Guarde o arquivo `.jks` e as credenciais em local corporativo protegido; o mesmo certificado será necessário nas próximas atualizações.

## Configuração principal

- Nome: `Portal Metaro`
- Pacote adotado: `br.com.metaro.portal`
- URL: `http://portal.metaro.com.br`
- Android mínimo: Android 8.0 (API 26)
- Versão atual: `1.4.0` (`versionCode 5`)

O projeto possui duas variantes de ambiente:

- `producao`: nome `Portal Metaro`, pacote `br.com.metaro.portal` e URL `http://portal.metaro.com.br`.
- `homologacao`: nome `Portal Metaro Teste`, pacote `br.com.metaro.portal.teste` e URL `http://192.168.1.237`.

No Android Studio, selecione `homologacaoDebug` em **Build Variants** para executar o ambiente de teste. As duas variantes podem permanecer instaladas simultaneamente no mesmo aparelho.

## APK de produção assinado

O APK distribuído aos usuários deve ser gerado como `producaoRelease` e assinado com uma chave permanente da empresa. No Android Studio, use **Build > Generate Signed Bundle / APK > APK** e escolha **Create new** na primeira geração.

Recomendações para a chave:

- Salve o `.jks` fora do projeto, em uma pasta corporativa protegida.
- Use o alias `portal-metaro` e uma senha forte guardada no cofre de senhas da empresa.
- Configure validade de pelo menos 25 anos.
- Selecione a variante `producaoRelease` e as assinaturas V2 e V3 quando disponíveis.
- Nunca distribua o `.jks`; entregue somente o APK assinado.

Todas as atualizações futuras de `br.com.metaro.portal` precisam usar essa mesma chave. O `.gitignore` já impede a inclusão acidental de arquivos `.jks` e `.keystore` no projeto.

Se o identificador corporativo desejado for diferente, altere `namespace`, `applicationId` e o pacote de `MainActivity.kt` antes de instalar a primeira versão nos aparelhos.

## Checklist de homologação

- Login, logout e expiração da sessão.
- Rotas internas e botão/gesto Voltar.
- Facebook, Instagram, LinkedIn, WhatsApp e suporte abrindo fora do app.
- Pesquisa e abertura de PDF com token autenticado.
- Escolha entre dois leitores de PDF, quando disponíveis.
- Visualização de `/images/{id}` em um aplicativo de imagens.
- Upload de arquivo.
- Abertura sem internet e recuperação após reconectar.
- Rotação de tela, teclado e preenchimento de formulários.
- Atualização do Angular no servidor sem reinstalar o APK.
