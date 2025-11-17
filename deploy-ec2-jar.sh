#!/bin/bash

# Script para deployar la aplicación CCaffa en EC2 sin Docker (JAR directo)
# Uso: ./deploy-ec2-jar.sh

set -e

echo "🚀 Iniciando deployment de CCaffa en EC2 (JAR)..."

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Compilar el proyecto
echo -e "${YELLOW}📦 Compilando proyecto con Maven...${NC}"
./mvnw clean package -DskipTests

# 2. Detener el proceso anterior si existe
echo -e "${YELLOW}🛑 Deteniendo proceso anterior...${NC}"
pkill -f "ccaffa-0.0.1-SNAPSHOT.jar" || true

# 3. Crear directorio para logs si no existe
mkdir -p logs

# 4. Iniciar la aplicación en background
echo -e "${YELLOW}🚢 Iniciando aplicación...${NC}"
nohup java -jar target/ccaffa-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://carflej.com.ar:3306/carflejc_demo_ccaffa \
  --spring.datasource.username=carflejc_desarrolladores \
  --spring.datasource.password=proyectofinal \
  --jwt.secret=ccaffaSecretKey1234567890123456789012345678901234567890 \
  --jwt.expiration=18000 \
  > logs/app.log 2>&1 &

APP_PID=$!
echo $APP_PID > app.pid

# 5. Verificar que la aplicación está corriendo
echo -e "${YELLOW}🔍 Verificando que la aplicación inició correctamente...${NC}"
sleep 10

if ps -p $APP_PID > /dev/null; then
   echo -e "${GREEN}✅ Deployment completado! PID: $APP_PID${NC}"
   echo -e "${GREEN}📡 La API está disponible en http://localhost:8080${NC}"
   echo ""
   echo "Comandos útiles:"
   echo "  - Ver logs: tail -f logs/app.log"
   echo "  - Ver PID: cat app.pid"
   echo "  - Detener: kill \$(cat app.pid)"
else
   echo -e "${RED}❌ Error: La aplicación no pudo iniciar${NC}"
   echo "Ver logs en: logs/app.log"
   exit 1
fi
