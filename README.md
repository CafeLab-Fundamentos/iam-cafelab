# IAM CafeLab - API Reference

<p style="color:#0A58CA;font-weight:700;font-size:16px;">
Referencia de endpoints del microservicio IAM CafeLab, organizada con formato tipo API Reference.
</p>

## Overview

- Microservicio: `iam-cafelab`
- Base path real: `/api/v1`
- Modulos documentados: `Authentication` y `Profiles`
- Formato de respuestas: `application/json`
- Autenticacion protegida: `Authorization: Bearer <jwt>`
- Swagger UI disponible en: `/swagger-ui/index.html`
- OpenAPI JSON disponible en: `/v3/api-docs`

## Reglas Generales

- `POST /api/v1/authentication/sign-in`, `POST /api/v1/authentication/sign-up` y `POST /api/v1/profiles` son publicos.
- El resto de endpoints exige JWT Bearer.
- Los endpoints con body esperan `Content-Type: application/json`.
- Los errores controlados suelen responder con `{"message":"..."}`.
- `GET /api/v1/profiles?email=...` usa el mismo recurso que `GET /api/v1/profiles`, pero cambia a busqueda por email cuando el query param `email` esta presente.
- En `GET /api/v1/profiles/{userId}` y `PATCH /api/v1/profiles/{userId}`, el parametro se llama `userId`, pero la implementacion actual consulta el `id` del perfil.

## Indice de Endpoints

| Modulo | Operacion | Metodo | Ruta | Auth |
| --- | --- | --- | --- | --- |
| Authentication | Iniciar sesion | `POST` | `/api/v1/authentication/sign-in` | Publico |
| Authentication | Registrar usuario IAM | `POST` | `/api/v1/authentication/sign-up` | Publico |
| Profiles | Crear perfil | `POST` | `/api/v1/profiles` | Publico |
| Profiles | Obtener perfil por id | `GET` | `/api/v1/profiles/{userId}` | Bearer JWT |
| Profiles | Obtener perfil por email | `GET` | `/api/v1/profiles?email={email}` | Bearer JWT |
| Profiles | Listar perfiles | `GET` | `/api/v1/profiles` | Bearer JWT |
| Profiles | Actualizar perfil | `PATCH` | `/api/v1/profiles/{userId}` | Bearer JWT |

---

<h2 style="color:#0A58CA;">Authentication - Iniciar sesion</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">POST</span>
<code>/api/v1/authentication/sign-in</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `email` | `string` | Requerido. Correo del usuario. La implementacion lo normaliza a minusculas. |
| `password` | `string` | Requerido. Contrasena en texto plano para autenticar. |

### Request Example

```json
{
  "email": "adrian@test.com",
  "password": "123456"
}
```

### Success Response

**HTTP 200**

```json
{
  "id": 12,
  "email": "adrian@test.com",
  "role": "barista",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZHJpYW5AdGVzdC5jb20ifQ.signature"
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del usuario autenticado. |
| `email` | `string` | Correo del usuario autenticado. |
| `role` | `string` | Rol almacenado para el usuario. |
| `token` | `string` | JWT Bearer generado por el backend. |

### Extra Response

No aplica.

### Error 4xx / 5xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Ejemplo documentable por el controller:

```json
{
  "message": "Credenciales invalidas"
}
```

Ejemplo observado en la implementacion actual cuando el servicio lanza `RuntimeException` por usuario inexistente o password invalido:

**HTTP 500**

```json
{
  "message": "Error interno del servidor"
}
```

---

<h2 style="color:#0A58CA;">Authentication - Registrar usuario IAM</h2>

<p>
<span style="background:#198754;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">POST</span>
<code>/api/v1/authentication/sign-up</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `email` | `string` | Requerido. Correo del usuario IAM. Se normaliza a minusculas. |
| `password` | `string` | Requerido. Contrasena en texto plano; el backend la almacena hasheada. |
| `role` | `string` | `Opcional`. Rol del usuario IAM. La implementacion no valida una lista cerrada en este endpoint. |

### Request Example

```json
{
  "email": "owner@test.com",
  "password": "123456",
  "role": "owner"
}
```

### Success Response

**HTTP 201**

```json
{
  "id": 13,
  "email": "owner@test.com",
  "role": "owner",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvd25lckB0ZXN0LmNvbSJ9.signature"
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del usuario creado. |
| `email` | `string` | Correo del usuario recien autenticado. |
| `role` | `string` | Rol guardado en IAM. |
| `token` | `string` | JWT generado despues del registro. |

### Extra Response

No aplica.

### Error 4xx / 5xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 400 posible por datos invalidos:

```json
{
  "message": "Email cannot be null or blank"
}
```

Error 500 posible en la implementacion actual si el correo ya existe:

```json
{
  "message": "Error interno del servidor"
}
```

---

<h2 style="color:#0A58CA;">Profiles - Crear perfil</h2>

<p>
<span style="background:#198754;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">POST</span>
<code>/api/v1/profiles</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `name` | `string` | Requerido. Nombre del perfil. |
| `email` | `string` | Requerido. Correo del perfil. |
| `password` | `string` | Requerido. Password usada para emitir el evento de creacion de usuario IAM. |
| `role` | `string` | Requerido. Solo acepta `barista` o `owner`. |
| `cafeteriaName` | `string` | `Opcional`. Nombre de la cafeteria. |
| `experience` | `string` | `Opcional`. Experiencia del usuario. |
| `profilePicture` | `string` | `Opcional`. URL o nombre del archivo de foto. |
| `paymentMethod` | `string` | `Opcional`. Metodo de pago asociado. |
| `isFirstLogin` | `boolean` | `Opcional`. Si se omite, el valor practico queda en `false`. |
| `plan` | `string` | `Opcional`. Nombre del plan de suscripcion. |
| `hasPlan` | `boolean` | `Opcional`. Si se omite, el valor practico queda en `false`. |

### Request Example

```json
{
  "name": "Adrian",
  "email": "adrian@test.com",
  "password": "123456",
  "role": "barista",
  "cafeteriaName": "CafeLab",
  "experience": "2 years",
  "profilePicture": "profile.png",
  "paymentMethod": "Visa",
  "isFirstLogin": true,
  "plan": "basic",
  "hasPlan": true
}
```

### Success Response

**HTTP 201**

```json
{
  "id": 7,
  "name": "Adrian",
  "email": "adrian@test.com",
  "role": "barista",
  "cafeteriaName": "CafeLab",
  "experience": "2 years",
  "profilePicture": "profile.png",
  "paymentMethod": "Visa",
  "plan": "basic",
  "hasPlan": true
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del perfil. |
| `name` | `string` | Nombre visible del perfil. |
| `email` | `string` | Correo del perfil. |
| `role` | `string` | Rol del perfil. |
| `cafeteriaName` | `string` | Nombre de la cafeteria asociada. |
| `experience` | `string` | Experiencia declarada. |
| `profilePicture` | `string` | Foto o referencia de imagen del perfil. |
| `paymentMethod` | `string` | Metodo de pago almacenado. |
| `plan` | `string` | Plan de suscripcion. |
| `hasPlan` | `boolean` | Indica si el perfil tiene plan. |

### Extra Response

No aplica.

### Error 4xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 400 por body invalido:

```json
{
  "message": "No se pudo leer el JSON: Name is required"
}
```

Error 400 por rol invalido:

```json
{
  "message": "No se pudo leer el JSON: Role must be either 'barista' or 'owner'"
}
```

Error 400 por correo duplicado:

```json
{
  "message": "Profile with email address already exists"
}
```

---

<h2 style="color:#0A58CA;">Profiles - Obtener perfil por id</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">GET</span>
<code>/api/v1/profiles/{userId}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Authorization` | Requerido. Formato `Bearer <jwt>`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. Nombre expuesto por la ruta; la implementacion actual lo usa para buscar el `id` del perfil. |

### Request Example

```http
GET /api/v1/profiles/7
Authorization: Bearer <jwt>
Accept: application/json
```

### Success Response

**HTTP 200**

```json
{
  "id": 7,
  "name": "Adrian",
  "email": "adrian@test.com",
  "role": "barista",
  "cafeteriaName": "CafeLab",
  "experience": "2 years",
  "profilePicture": "profile.png",
  "paymentMethod": "Visa",
  "plan": "basic",
  "hasPlan": true
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del perfil. |
| `name` | `string` | Nombre del perfil. |
| `email` | `string` | Correo del perfil. |
| `role` | `string` | Rol del perfil. |
| `cafeteriaName` | `string` | Cafeteria asociada. |
| `experience` | `string` | Experiencia declarada. |
| `profilePicture` | `string` | Foto o referencia visual del perfil. |
| `paymentMethod` | `string` | Metodo de pago almacenado. |
| `plan` | `string` | Plan actual. |
| `hasPlan` | `boolean` | Indica si el perfil tiene plan. |

### Extra Response

No aplica.

### Error 4xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 401:

```json
{
  "message": "Autenticacion requerida o token invalido"
}
```

Error 404:

```json
{
  "message": "Perfil no encontrado"
}
```

---

<h2 style="color:#0A58CA;">Profiles - Obtener perfil por email</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">GET</span>
<code>/api/v1/profiles?email={email}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Authorization` | Requerido. Formato `Bearer <jwt>`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `email` | `string` | Requerido. Query param del correo a buscar. Se compara normalizado en minusculas. |

### Request Example

```http
GET /api/v1/profiles?email=batman@test.com
Authorization: Bearer <jwt>
Accept: application/json
```

### Success Response

**HTTP 200**

```json
{
  "id": 9,
  "name": "Batman",
  "email": "batman@test.com",
  "role": "owner",
  "cafeteriaName": "La Baticueva",
  "experience": "3 years",
  "profilePicture": "batman.png",
  "paymentMethod": "Mastercard",
  "plan": "premium",
  "hasPlan": true
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del perfil. |
| `name` | `string` | Nombre del perfil encontrado. |
| `email` | `string` | Correo buscado. |
| `role` | `string` | Rol del perfil. |
| `cafeteriaName` | `string` | Cafeteria asociada. |
| `experience` | `string` | Experiencia declarada. |
| `profilePicture` | `string` | Foto o referencia visual del perfil. |
| `paymentMethod` | `string` | Metodo de pago asociado. |
| `plan` | `string` | Plan actual. |
| `hasPlan` | `boolean` | Indica si el perfil tiene plan. |

### Extra Response

No aplica.

### Error 4xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 401:

```json
{
  "message": "Autenticacion requerida o token invalido"
}
```

Error 404:

```json
{
  "message": "Perfil no encontrado para el email indicado"
}
```

---

<h2 style="color:#0A58CA;">Profiles - Listar perfiles</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">GET</span>
<code>/api/v1/profiles</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Authorization` | Requerido. Formato `Bearer <jwt>`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `Sin parametros` | `-` | Este endpoint lista todos los perfiles y no maneja paginacion ni filtros adicionales. |

### Request Example

```http
GET /api/v1/profiles
Authorization: Bearer <jwt>
Accept: application/json
```

### Success Response

**HTTP 200**

```json
[
  {
    "id": 7,
    "name": "Adrian",
    "email": "adrian@test.com",
    "role": "barista",
    "cafeteriaName": "CafeLab",
    "experience": "2 years",
    "profilePicture": "profile.png",
    "paymentMethod": "Visa",
    "plan": "basic",
    "hasPlan": true
  },
  {
    "id": 9,
    "name": "Batman",
    "email": "batman@test.com",
    "role": "owner",
    "cafeteriaName": "La Baticueva",
    "experience": "3 years",
    "profilePicture": "batman.png",
    "paymentMethod": "Mastercard",
    "plan": "premium",
    "hasPlan": true
  }
]
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `[]` | `array` | Arreglo de objetos `ProfileResource`. |

### Extra Response

#### Extra Profile Item Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del perfil. |
| `name` | `string` | Nombre del perfil. |
| `email` | `string` | Correo del perfil. |
| `role` | `string` | Rol del perfil. |
| `cafeteriaName` | `string` | Cafeteria asociada. |
| `experience` | `string` | Experiencia declarada. |
| `profilePicture` | `string` | Foto o referencia visual del perfil. |
| `paymentMethod` | `string` | Metodo de pago del perfil. |
| `plan` | `string` | Plan del perfil. |
| `hasPlan` | `boolean` | Indica si el perfil tiene plan. |

### Error 4xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 401:

```json
{
  "message": "Autenticacion requerida o token invalido"
}
```

---

<h2 style="color:#0A58CA;">Profiles - Actualizar perfil</h2>

<p>
<span style="background:#FD7E14;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">PATCH</span>
<code>/api/v1/profiles/{userId}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Authorization` | Requerido. Formato `Bearer <jwt>`. |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. Nombre expuesto por la ruta; la implementacion actual lo usa para buscar el `id` del perfil. |
| `name` | `string` | `Opcional`. Si llega, actualiza el nombre. |
| `email` | `string` | `Opcional`. Si cambia, se normaliza a minusculas y publica un evento de cambio de email. |
| `cafeteriaName` | `string` | `Opcional`. Actualiza la cafeteria. |
| `experience` | `string` | `Opcional`. Actualiza la experiencia. |
| `paymentMethod` | `string` | `Opcional`. Actualiza el metodo de pago. |
| `isFirstLogin` | `boolean` | `Opcional`. Si se omite, no cambia. |
| `plan` | `string` | `Opcional`. Actualiza el plan. |
| `hasPlan` | `boolean` | `Opcional`. Si se omite, no cambia. |

### Request Example

```json
{
  "name": "Bruce Wayne",
  "email": "bruce@test.com",
  "cafeteriaName": "Wayne Coffee",
  "experience": "5 years",
  "paymentMethod": "Mastercard",
  "isFirstLogin": false,
  "plan": "premium",
  "hasPlan": true
}
```

### Success Response

**HTTP 200**

```json
{
  "id": 7,
  "name": "Bruce Wayne",
  "email": "bruce@test.com",
  "role": "owner",
  "cafeteriaName": "Wayne Coffee",
  "experience": "5 years",
  "profilePicture": "bruce.png",
  "paymentMethod": "Mastercard",
  "plan": "premium",
  "hasPlan": true
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador del perfil actualizado. |
| `name` | `string` | Nombre final del perfil. |
| `email` | `string` | Correo final del perfil. |
| `role` | `string` | Rol actual del perfil. |
| `cafeteriaName` | `string` | Cafeteria final del perfil. |
| `experience` | `string` | Experiencia final del perfil. |
| `profilePicture` | `string` | Foto actual del perfil. |
| `paymentMethod` | `string` | Metodo de pago final. |
| `plan` | `string` | Plan final. |
| `hasPlan` | `boolean` | Estado final del plan. |

### Extra Response

No aplica.

### Error 4xx

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `message` | `string` | Mensaje simple de error. |

Error 401:

```json
{
  "message": "Autenticacion requerida o token invalido"
}
```

Error 404:

```json
{
  "message": "Perfil no encontrado"
}
```

Error 400 por body mal formado:

```json
{
  "message": "No se pudo leer el JSON: Cannot deserialize value"
}
```

## Notas Finales de Implementacion

- `AuthenticationController` y `ProfilesController` son los unicos controllers REST del proyecto para el contrato publico.
- `role` puede viajar en `sign-up`, pero en `create profile` si existe validacion explicita y solo acepta `barista` u `owner`.
- `profilePicture` se devuelve en `ProfileResource`, pero no forma parte del body de `PATCH /api/v1/profiles/{userId}`.
- `isFirstLogin` existe en create/update, pero no se expone en `ProfileResource`.