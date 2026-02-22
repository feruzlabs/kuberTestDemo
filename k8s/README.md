# Kubernetes + HTTPS (TLS)

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
