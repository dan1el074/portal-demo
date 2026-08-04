import { execFileSync } from 'node:child_process';
import { mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const frontendDir = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const packageJson = JSON.parse(readFileSync(resolve(frontendDir, 'package.json'), 'utf8'));

let revision = process.env.APP_BUILD_VERSION?.trim();

if (!revision) {
  revision = execFileSync('git', ['rev-parse', '--short=12', 'HEAD'], {
    cwd: frontendDir,
    encoding: 'utf8'
  }).trim();
}

const version = `${packageJson.version}+${revision}`;
const generatedAt = new Date().toISOString();
const typescriptPath = resolve(frontendDir, 'src/app/generated/build-version.ts');
const jsonPath = resolve(frontendDir, 'src/assets/app-version.json');

mkdirSync(dirname(typescriptPath), { recursive: true });

writeFileSync(
  typescriptPath,
  `// Gerado automaticamente antes do build.\nexport const BUILD_VERSION = ${JSON.stringify(version)};\n`,
  'utf8'
);

writeFileSync(
  jsonPath,
  `${JSON.stringify({ version, generatedAt }, null, 2)}\n`,
  'utf8'
);

console.log(`Versão da aplicação gerada: ${version}`);
