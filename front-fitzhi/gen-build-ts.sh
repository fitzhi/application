echo "Generation of the Application build timestamp"
echo "---------------------------------------------"
echo "export const RunTimeFile = { buildtime: '$(date '+%Y-%m-%d %H:%M')' };" | tee src/environments/runtime-file.ts