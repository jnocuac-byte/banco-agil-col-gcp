# Usamos Nginx Alpine por ser liviano y rápido para servir estáticos.
FROM nginx:alpine

# 1. Copia el archivo de configuración corregido (con listen 8080)
# Este archivo es CRÍTICO para que Nginx escuche en el puerto 8080.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 2. Copia tus archivos estáticos
# El '.' se refiere a la carpeta 'frontend' porque usamos '--source ./frontend' en el comando.
COPY . /usr/share/nginx/html/

# 3. Informamos a Docker que este contenedor escucha en el puerto 8080
EXPOSE 8080

# Comando de inicio predeterminado de Nginx.
CMD ["nginx", "-g", "daemon off;"]
