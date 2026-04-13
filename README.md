# Franquicias Backend API (Spring WebFlux + MongoDB)

API reactiva para administrar franquicias, sus sucursales y productos con stock.

## Stack

- Java 21
- Spring Boot 3 (WebFlux)
- MongoDB
- Docker / Docker Compose
- Terraform (IaC)
- Railway (despliegue en nube)

## Requisitos

- Docker Desktop
- Java 21
- Maven Wrapper (`mvnw.cmd`)

## Ejecucion local

### Opcion 1: Docker Compose (app + mongo)

```powershell
docker compose up -d --build
```

App: `http://localhost:8080`  
MongoDB: `localhost:27017`

Para detener:

```powershell
docker compose down
```

### Opcion 2: Mongo en Docker + app en local

1. Levantar solo Mongo:

```powershell
docker compose up -d mongodb
```

2. Ejecutar la app:

```powershell
.\mvnw.cmd spring-boot:run
```

## Variables de entorno

- `MONGODB_URI` (default: `mongodb://admin:admin@localhost:27017/franquiciasdb?authSource=admin`)
- `PORT` o `SERVER_PORT` (default: `8080`)

## Despliegue en la nube (Railway + MongoDB Atlas)

La API se encuentra desplegada y disponible publicamente. Puedes verificar que esta activa accediendo al health check desde el navegador:

**https://franquicias-backend-production.up.railway.app/health**


Para probar los endpoints de negocio se recomienda usar Postman o curl, ya que requieren metodos HTTP como POST, PATCH y DELETE.

### Si desea realizar su propio despliegue en la nube, sigue estos pasos:

#### 1) Crear MongoDB Atlas

1. Crear cuenta en [mongodb.com/atlas](https://mongodb.com/atlas) y crear un cluster **M0 (gratuito)**.
2. Crear un usuario de base de datos con permisos de **Atlas Admin** y guardar la contrasena.
3. En **Network Access**, agregar `0.0.0.0/0` para permitir conexiones desde Railway.
4. Copiar el connection string desde **Connect → Drivers** y armar la URI con este formato:

```
mongodb+srv://<user>:<password>@<cluster-url>/franquiciasdb?retryWrites=true&w=majority&appName=Cluster0
```

#### 2) Desplegar en Railway

1. Crear cuenta en [railway.app](https://railway.app) usando GitHub.
2. Click en **New Project** → **GitHub Repository** → seleccionar este repositorio.
3. Railway detecta el `Dockerfile` y el `railway.json` automaticamente.
4. En el tab **Variables**, configurar las siguientes variables de entorno:
   - `MONGODB_URI` = connection string de Atlas con usuario y contrasena reales
   - `PORT` = `8080` (opcional, Railway lo inyecta automaticamente)
5. Railway dispara el deploy automaticamente al guardar las variables.
6. Una vez finalizado, ir a **Settings → Networking → Generate Domain** para obtener la URL publica.

#### 3) Validar el despliegue

```bash
# Health check
curl https://<tu-app>.up.railway.app/health
# Respuesta esperada: {"status":"UP"}

# Crear una franquicia
curl -X POST https://<tu-app>.up.railway.app/api/franquicias \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Mi Franquicia"}'
```

## IaC de persistencia (Terraform)

Se incluye aprovisionamiento de MongoDB con Terraform en `infra/terraform`.

### Requisitos IaC

- Terraform >= 1.6
- Docker Desktop encendido

### Pasos

1. Entrar a la carpeta:

```powershell
cd infra\terraform
```

2. (Opcional) crear variables personalizadas:

```powershell
Copy-Item terraform.tfvars.example terraform.tfvars
```

3. Inicializar Terraform:

```powershell
terraform init
```

4. Ver plan:

```powershell
terraform plan
```

5. Aplicar infraestructura:

```powershell
terraform apply -auto-approve
```

6. Ver outputs:

```powershell
terraform output
```

7. Destruir infraestructura al terminar:

```powershell
terraform destroy -auto-approve
```

## Endpoints

Base URL local: `http://localhost:8080/api/franquicias`  
Base URL nube: `https://franquicias-backend-production.up.railway.app/api/franquicias`

### 1) Crear franquicia

- `POST /api/franquicias`
- Body:

```json
{
  "nombre": "Franquicia Centro"
}
```

### 2) Agregar sucursal a franquicia

- `POST /api/franquicias/{franquiciaId}/sucursales`
- Body:

```json
{
  "nombre": "Sucursal Norte"
}
```

### 3) Agregar producto a sucursal

- `POST /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos`
- Body:

```json
{
  "nombre": "Coca Cola",
  "stock": 25
}
```

### 4) Eliminar producto de sucursal

- `DELETE /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`

### 5) Actualizar stock de producto

- `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock`
- Body:

```json
{
  "stock": 40
}
```

### 6) Producto con mayor stock por sucursal

- `GET /api/franquicias/{franquiciaId}/productos/max-stock-por-sucursal`

Respuesta ejemplo:

```json
[
  {
    "sucursalId": "daf9076e-bed4-45d5-8587-35b1195603bf",
    "sucursalNombre": "Sucursal Norte",
    "productoId": "c74a8ed0-6bf0-48c2-a765-387ed2a69afa",
    "productoNombre": "Coca Cola",
    "stock": 40
  }
]
```

## Endpoints extra

- `PATCH /api/franquicias/{franquiciaId}/nombre`
- `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre`
- `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre`

Body para renombrar:

```json
{
  "nombre": "Nuevo Nombre"
}
```

## Prueba manual sugerida

1. Crear franquicia.
2. Agregar sucursal.
3. Agregar 2 productos con stocks distintos.
4. Actualizar stock de un producto.
5. Consultar maximo stock por sucursal.
6. Eliminar producto y confirmar en nueva consulta.
7. Probar validaciones (`nombre` vacio, `stock` negativo).
