#!/bin/bash

# Script para deployar la aplicación CCaffa en EC2 con Docker
# Uso: ./deploy-ec2-docker.sh

set -e

echo "🚀 Iniciando deployment de CCaffa en EC2..."

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Construir la imagen Docker
echo -e "${YELLOW}📦 Construyendo imagen Docker...${NC}"
docker build -t ccaffa-api:latest .

# 2. Detener contenedor anterior si existe
echo -e "${YELLOW}🛑 Deteniendo contenedor anterior...${NC}"
docker stop ccaffa-api 2>/dev/null || true
docker rm ccaffa-api 2>/dev/null || true

# 3. Iniciar el nuevo contenedor
echo -e "${YELLOW}🚢 Iniciando contenedor...${NC}"
docker run -d \
  --name ccaffa-api \
  -p 8080:8080 \
  -v $(pwd)/storage:/app/storage \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://carflej.com.ar:3306/carflejc_control_calidad \
  -e SPRING_DATASOURCE_USERNAME=carflejc_desarrolladores \
  -e SPRING_DATASOURCE_PASSWORD=proyectofinal \
  -e JWT_SECRET=ccaffaSecretKey1234567890123456789012345678901234567890 \
  -e JWT_EXPIRATION=18000 \
  --restart unless-stopped \
  ccaffa-api:latest

# 4. Verificar que el contenedor está corriendo
echo -e "${YELLOW}🔍 Verificando estado del contenedor...${NC}"
sleep 5
docker ps | grep ccaffa-api

echo -e "${GREEN}✅ Deployment completado!${NC}"
echo -e "${GREEN}📡 La API está disponible en http://localhost:8080${NC}"
echo ""
echo "Comandos útiles:"
echo "  - Ver logs: docker logs -f ccaffa-api"
echo "  - Ver estado: docker ps"
echo "  - Detener: docker stop ccaffa-api"
echo "  - Reiniciar: docker restart ccaffa-api"
