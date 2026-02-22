# GitHub Actions — Secrets va variables

Repo: **Settings** → **Secrets and variables** → **Actions**

---

## Secrets (maxfiy)

Bu qiymatlar workflow da ishlatiladi; logda ko‘rinmaydi.

| Secret nomi           | Qayerdan olish | Misol qiymat (ko‘rsatish mumkin emas) |
|----------------------|----------------|----------------------------------------|
| `DOCKERHUB_USERNAME` | Docker Hub loginingiz | `feruzlabs` |
| `DOCKERHUB_TOKEN`    | Docker Hub → Account Settings → Security → **New Access Token** (Read & Write) | `dckr_pat_xxxx...` |

### DOCKERHUB_TOKEN yaratish

1. https://hub.docker.com → Log in
2. **Account Settings** → **Security** → **New Access Token**
3. Label: masalan `github-actions`
4. Permissions: **Read, Write, Delete**
5. Generate → token ni nusxalang va GitHub da **Actions → Secrets** ga `DOCKERHUB_TOKEN` nomi bilan qo‘ying

---

## Variables (ochiq, ixtiyoriy)

Agar image nomi yoki registry ni workflow dan o‘zgartirmoqchi bo‘lsangiz, **Variables** da qo‘yishingiz mumkin (keyin workflow da `vars.XXX` ishlatiladi). Hozirgi workflow da bular faylda `env:` ichida:

| O‘zgaruvchi   | Hozirgi qiymat (workflow da) | Izoh |
|---------------|------------------------------|------|
| `REGISTRY`    | `docker.io`                  | Docker Hub |
| `IMAGE_NAME`  | `feruzlabs/kuber-test-demo`  | `username/repo` |

Bularni **Variables** ga ko‘chirmasangiz ham ishlaydi; o‘zgartirmoqchi bo‘lsangiz workflow faylida yoki repo variables da yangilang.

---

## Tekshirish

- **Secrets** to‘g‘ri bo‘lsa: `main`/`master` ga push qilganda workflow “Log in to Docker Hub” qadamida xato bermasligi va image push bo‘lishi kerak.
- Xato bo‘lsa: Actions → failed run → “Log in to Docker Hub” step logini ko‘ring (parol yopiq, lekin “unauthorized” kabi xabarlar chiqadi).
