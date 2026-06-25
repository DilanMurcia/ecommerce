# ecommerce

Ecommerce platform for print-on-demand (POD) products, built as a portfolio project
and a real business. Sells custom-designed apparel and accessories to customers in
the US and globally, using dropshipping fulfillment.

## Stack

| Layer | Technology | Purpose |
|---|---|---|
| Storefront | Astro + Tailwind CSS | Fast, SEO-friendly pages |
| Interactivity | React (Astro islands) | Cart, checkout, filters |
| Backend API | Spring Boot 3 + Java 21 | REST API, business logic |
| Database | PostgreSQL (Neon) | Products, orders, users |
| Auth | Spring Security + JWT | Sessions and admin access |
| Payments | Stripe Checkout | USD card payments |
| Dropshipping | Printify API | Product fulfillment |
| Email | Resend | Transactional emails |
| Frontend host | Vercel | CDN, free tier |
| Backend host | Railway | Spring Boot, free tier |

## Monorepo structure

```
ecommerce/
├── storefront/        # Astro frontend
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── layouts/
│   │   └── lib/
│   └── public/
│
└── api/               # Spring Boot backend
    └── src/main/java/com/ecommerce/
        ├── controllers/
        ├── services/
        ├── repositories/
        ├── entities/
        ├── dto/
        └── config/
```

## Quick start

### Prerequisites
- Node.js 20+
- Java 21
- Maven 3.9+
- Accounts: GitHub, Neon, Vercel, Railway, Stripe, Printify, Resend

### Setup

```bash
# Clone
git clone git@github.com:DilanMurcia/ecommerce.git
cd ecommerce

# Frontend (Astro) - to be initialized in Issue #2
cd storefront
npm install
npm run dev          # http://localhost:4321

# Backend (Spring Boot) - to be initialized in Issue #3
cd ../api
./mvnw spring-boot:run   # http://localhost:8080
```

## Environment variables

Copy `.env.example` to `.env` in each project and fill in the values.
Real `.env` files are ignored by Git.

## Project methodology

This project follows a **Kanban** approach with a 3-column board (Backlog / In
progress / Done), WIP limit of 2, and a Definition of Done for every task.
Issues live in the GitHub Projects board. See the project guide for the full
roadmap by phase.

## Phases

- **Phase 0** — Setup (current)
- **Phase 1** — Catalog and storefront
- **Phase 2** — Cart and checkout
- **Phase 3** — Printify integration and niche discovery
- **Phase 4** — Auth and admin panel
- **Phase 5** — Polish, deploy, and portfolio
