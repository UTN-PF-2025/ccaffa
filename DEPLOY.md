# Guía de Deployment en AWS EC2

## 📋 Requisitos Previos
- Instancia EC2 con Ubuntu 22.04+ (t2.medium o superior recomendado por Java 21)
- Security Group configurado con puerto 8080 abierto
- Acceso SSH a la instancia

---

## 🐳 Opción 1: Deployment con Docker (Recomendado)

### Paso 1: Instalar Docker en EC2

```bash
# Conectar a la EC2
ssh -i tu-key.pem ubuntu@tu-ec2-ip

# Actualizar paquetes
sudo apt update && sudo apt upgrade -y

# Instalar Docker
sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker

# Agregar usuario al grupo docker (evita usar sudo)
sudo usermod -aG docker ubuntu

# Cerrar sesión y volver a conectar para aplicar cambios
exit
```

### Paso 2: Subir el código a EC2

**Opción A: Con Git (Recomendado)**
```bash
ssh -i tu-key.pem ubuntu@tu-ec2-ip
cd ~
git clone [URL_DE_TU_REPOSITORIO]
cd ccaffa
```

**Opción B: Con SCP**
```bash
# Desde tu máquina local
scp -i tu-key.pem -r /ruta/local/ccaffa ubuntu@tu-ec2-ip:~/
```

### Paso 3: Deploy con Docker

```bash
# En la EC2, dentro del directorio del proyecto
chmod +x deploy-ec2-docker.sh
./deploy-ec2-docker.sh
```

### Paso 4: Verificar

```bash
# Ver logs en tiempo real
docker logs -f ccaffa-api

# Verificar que está corriendo
curl http://localhost:8080/actuator/health

# Ver estado del contenedor
docker ps
```

### Comandos Útiles Docker

```bash
# Reiniciar la aplicación
docker restart ccaffa-api

# Detener la aplicación
docker stop ccaffa-api

# Ver logs
docker logs -f ccaffa-api

# Acceder al contenedor
docker exec -it ccaffa-api bash

# Limpiar contenedores antiguos
docker system prune -a
```

---

## ☕ Opción 2: Deployment sin Docker (JAR directo)

### Paso 1: Instalar Java 21 en EC2

```bash
# Conectar a la EC2
ssh -i tu-key.pem ubuntu@tu-ec2-ip

# Instalar Java 21
sudo apt update
sudo apt install -y openjdk-21-jdk maven

# Verificar instalación
java -version
mvn -version
```

### Paso 2: Subir el código (igual que Opción 1)

### Paso 3: Deploy con JAR

```bash
# En la EC2, dentro del directorio del proyecto
chmod +x deploy-ec2-jar.sh
./deploy-ec2-jar.sh
```

### Paso 4: (Opcional) Configurar como servicio systemd

```bash
# Crear directorio para logs
sudo mkdir -p /var/log/ccaffa
sudo chown ubuntu:ubuntu /var/log/ccaffa

# Copiar el archivo de servicio
sudo cp ccaffa.service /etc/systemd/system/

# IMPORTANTE: Editar el archivo para ajustar las rutas
sudo nano /etc/systemd/system/ccaffa.service
# Cambiar /home/ubuntu/ccaffa por tu ruta real si es diferente

# Recargar systemd
sudo systemctl daemon-reload

# Habilitar e iniciar el servicio
sudo systemctl enable ccaffa
sudo systemctl start ccaffa

# Ver estado
sudo systemctl status ccaffa

# Ver logs
sudo journalctl -u ccaffa -f
```

### Comandos Útiles Systemd

```bash
# Ver estado del servicio
sudo systemctl status ccaffa

# Reiniciar
sudo systemctl restart ccaffa

# Detener
sudo systemctl stop ccaffa

# Ver logs
sudo journalctl -u ccaffa -f

# Ver logs desde el inicio
sudo journalctl -u ccaffa --no-pager
```

---

## 🔒 Configuración del Security Group de AWS

Tu Security Group debe tener las siguientes reglas de entrada:

| Tipo | Protocolo | Puerto | Origen | Descripción |
|------|-----------|--------|--------|-------------|
| SSH | TCP | 22 | Tu IP | Acceso SSH |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | API REST |

**Nota:** Si el frontend está en otra EC2 en la misma VPC, puedes restringir el puerto 8080 solo al Security Group del frontend.

---

## 🌐 Acceso desde el Frontend

### Si frontend y backend están en EC2s diferentes en la MISMA VPC:
```javascript
// Usar la IP privada de la EC2 del backend
const API_URL = 'http://10.0.1.x:8080/api';
```

### Si están en EC2s diferentes en VPCs diferentes:
```javascript
// Usar la IP pública o elastic IP del backend
const API_URL = 'http://ec2-xx-xxx-xxx-xxx.compute-1.amazonaws.com:8080/api';
```

### Si frontend y backend están en la MISMA EC2:
```javascript
// Usar localhost
const API_URL = 'http://localhost:8080/api';
```

---

## 🔧 Troubleshooting

### La aplicación no inicia
```bash
# Docker
docker logs ccaffa-api

# JAR directo
tail -f logs/app.log

# Systemd
sudo journalctl -u ccaffa -f
```

### No puedo conectarme desde el frontend
1. Verificar Security Group: puerto 8080 abierto
2. Verificar que la app está corriendo: `curl http://localhost:8080/actuator/health`
3. Verificar firewall en EC2: `sudo ufw status`
4. Verificar IP correcta (privada vs pública)

### Error de memoria/OOM
```bash
# Agregar configuración de JVM en el comando de inicio
# Docker: modificar deploy-ec2-docker.sh
-e JAVA_OPTS="-Xmx512m -Xms256m"

# JAR directo: modificar deploy-ec2-jar.sh o ccaffa.service
java -Xmx512m -Xms256m -jar ...
```

### Puerto 8080 ya en uso
```bash
# Encontrar qué proceso usa el puerto
sudo lsof -i :8080

# Matar el proceso
sudo kill -9 [PID]
```

---

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Respuesta esperada:
```json
{
  "status": "UP"
}
```

### Verificar uso de recursos
```bash
# CPU y Memoria
htop

# Docker stats
docker stats ccaffa-api

# Espacio en disco
df -h
```

---

## 🔄 Actualizar la Aplicación

### Con Docker:
```bash
cd ~/ccaffa
git pull  # o actualizar código
./deploy-ec2-docker.sh
```

### Con JAR directo:
```bash
cd ~/ccaffa
git pull
./deploy-ec2-jar.sh
```

### Con Systemd:
```bash
cd ~/ccaffa
git pull
./mvnw clean package -DskipTests
sudo systemctl restart ccaffa
```

---

## 💡 Recomendaciones de Producción

1. **Usa Elastic IP** para que la IP pública no cambie
2. **Configura HTTPS** con un certificado SSL (Let's Encrypt)
3. **Usa un Load Balancer** si necesitas alta disponibilidad
4. **Configura backups** automáticos con AWS Backup
5. **Monitorea logs** con CloudWatch
6. **Usa RDS** para MySQL en lugar de DB externa
7. **Considera ECS/EKS** para deployment más robusto
8. **Configura auto-scaling** si esperas tráfico variable
