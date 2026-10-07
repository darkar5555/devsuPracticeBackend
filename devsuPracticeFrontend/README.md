# devsu-practice-frontend

Angular 22 client of the Devsu Bank API.

```bash
npm install
npm start      # http://localhost:4200, /api proxied to http://localhost:8080
npm test       # Jest
```

```
src/app
  api/        HTTP services and error messages
  models/     API types
  shared/     search box, validation messages
  customers/ accounts/ transactions/ reports/   list and form per section
```
