# Kubernetes + HTTPS (TLS)

## Trafik oqimi va IP’lar

- **10.43.144.212** — Service’ning **ClusterIP** (faqat kluster ichida). Buni o‘zgartirish shart emas; pod’larga trafik shu orqali boradi.
- **144.91.116.93** — **tashqi IP** (demo.feruzlabs.dev shu IP’ga resolve bo‘ladi). Kirish shu manzilda bo‘lishi kerak.

Oqim: **Foydalanuvchi → 144.91.116.93 (HTTPS) → Ingress Controller → Service 10.43.144.212:80 → Pod.**

Demak, **144.91.116.93** da Ingress Controller (masalan, nginx ingress) tinglashi kerak. Buning uchun:

1. **Ingress Controller o‘zi 144.91.116.93 da bo‘lsa** (LoadBalancer va provider shu IP’ni bergan): hech narsa qilish shart emas.
2. **144.91.116.93 boshqa server (proxy) bo‘lsa**: o‘sha serverda `demo.feruzlabs.dev` uchun reverse proxy sozlang va trafikni klusterdagi Ingress Controller’ga yuboring (masalan, NodePort yoki LoadBalancer IP:port).
3. **LoadBalancer’ga aniq IP berish** (masalan, cloud’da rezerv qilgan 144.91.116.93): Ingress Controller’ning **Service** manifestida `loadBalancerIP: 144.91.116.93` qo‘ying (provider qo‘llab-quvvatlasa).

Tekshirish:
```bash
kubectl get svc -A | grep -i ingress
```
Ingress Controller Service’ning EXTERNAL-IP yoki LoadBalancer’i 144.91.116.93 bo‘lishi kerak (yoki 144.91.116.93 proxy orqali shu servisga yo‘naltirilgan bo‘lishi kerak).

---

## Talablar

- **cert-manager** klusterda o‘rnatilgan bo‘lishi kerak:
  ```bash
  kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml
  ```

## Qaysi issuer ishlatish

| Holat | Issuer | O‘zgarish |
|--------|--------|-----------|
| **Production**: `demo.feruzlabs.dev` public DNS, Ingress’ga tashqaridan kirish mumkin | Let’s Encrypt | `certificate.yaml` da `issuerRef.name: letsencrypt-prod` (default). `cluster-issuer.yaml` da email o‘zgartiring. |
| **Lokal** (minikube/kind, DNS faqat hosts yoki lokal) | Self-signed | `certificate.yaml` da `issuerRef.name: selfsigned-issuer` qiling. Brauzer “xavfsiz emas” deydi — “Advance” → “Proceed” yoki CA’ni trust qiling. |

## Apply tartibi

```bash
# 1) ClusterIssuer(lar)
kubectl apply -f k8s/cluster-issuer.yaml

# 2) Certificate (cert-manager Secret yaratadi)
kubectl apply -f k8s/certificate.yaml

# 3) Ingress (TLS secretName: demo-feruzlabs-dev-tls)
kubectl apply -f k8s/ingress.yaml
```

Sertifikat tayyor bo‘lishini tekshirish:

```bash
kubectl get certificate
kubectl describe certificate demo-feruzlabs-dev-tls
```

`Ready: True` bo‘lsa, Ingress HTTPS orqali ishlaydi.

## Lokal + brauzer xatosiz ko‘rinishi

Self-signed ishlatilsa, brauzer ishonmaydi. Ikkita yo‘l:

1. **Istisno qo‘yish**: brauzerda “Advanced” → “Proceed to demo.feruzlabs.dev (unsafe)”.
2. **CA’ni ishonchli qilish**: cert-manager’da “SelfSigned” o‘rniga o‘z CA’ingiz bilan sign qiling (yoki `mkcert` bilan mahalliy CA yaratib, shu CA’dan cert chiqarib Ingress’da ishlating).

### HSTS xatosi (Chrome “Proceed” bermasa)

Chrome domen uchun HSTS saqlagan bo‘lsa, self-signed cert bilan ham “Proceed” ishlamaydi. HSTS ni o‘chirish:

1. Chrome’da `chrome://net-internals/#hsts` oching.
2. **Delete domain security policies** qismida `demo.feruzlabs.dev` yozib **Delete** bosing.
3. Sahifani qayta oching; endi “Proceed” paydo bo‘lishi mumkin.
