# Kubernetes Deploy — Tutorial

## Trafik oqimi

```
Foydalanuvchi → 144.91.116.93:443 (HTTPS)
  → Ingress Controller (nginx) — TLS termination, sertifikat
    → Service kuber-test-demo:443
      → Pod:8010
```

---

## 1. cert-manager o'rnatish (TLS uchun)

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml

# Ready bo'lishini kuting (~60 sekund)
kubectl wait --namespace cert-manager \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/instance=cert-manager \
  --timeout=120s
```

---

## 2. Ingress NGINX Controller o'rnatish

### A) Rasmiy manifest bilan:
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/cloud/deploy.yaml
```

### B) LoadBalancerIP ni 144.91.116.93 ga o'rnatish

**Yangi o'rnatish:** (bu loyiha fayli orqali)
```bash
kubectl apply -f k8s/ingress-nginx-loadbalancer.yaml
```

**Mavjud ingress-nginx'ga patch berish:**
```bash
# 1) Namespace va service nomini aniqlang:
kubectl get svc -A | grep ingress

# 2) MetalLB (v0.13+) — annotatsiya bilan (zamonaviy usul):
kubectl annotate svc ingress-nginx-controller -n ingress-nginx \
  metallb.universe.tf/loadBalancerIPs=144.91.116.93

# 3) K3s built-in ServiceLB bo'lsa, node label qo'yish:
kubectl label node <NODE_NAME> svccontroller.k3s.cattle.io/enablelb=true
```

LoadBalancer IP tasdiqlash:
```bash
kubectl get svc ingress-nginx-controller -n ingress-nginx
# EXTERNAL-IP ustunida 144.91.116.93 ko'rinishi kerak
```

> **K3s / bare-metal eslatma:** K3s built-in Klipper Load Balancer ishlaydi, lekin `loadBalancerIP` faqat bitta IP olishi mumkin. Agar MetalLB ishlatilsa, IP pool'da 144.91.116.93 bo'lishi kerak.

---

## 3. ClusterIssuer va Certificate yaratish

### Qaysi issuer?

| Holat | Issuer |
|--------|--------|
| Public domen, tashqaridan 80/443 ochiq | `letsencrypt-prod` (brauzer ishonadi) |
| Lokal / test cluster | `selfsigned-issuer` (brauzer ogohlantiradi) |

### cluster-issuer.yaml da emailni o'zgartiring:
```yaml
email: admin@feruzlabs.dev   # o'z emailingizni yozing
```

### certificate.yaml da issuer tanlang:
```yaml
# Public:
issuerRef:
  name: letsencrypt-prod
  kind: ClusterIssuer

# Lokal:
issuerRef:
  name: selfsigned-issuer
  kind: ClusterIssuer
```

### Apply:
```bash
kubectl apply -f k8s/cluster-issuer.yaml
kubectl apply -f k8s/certificate.yaml

# Sertifikat Ready bo'lishini kuting:
kubectl get certificate -n default
# READY = True bo'lishi kerak
```

---

## 4. Ilovani deploy qilish

### Image build va push:
```bash
# Loyiha root'ida:
docker build -t docker.io/feruzlabs/kuber-test-demo:latest .
docker push docker.io/feruzlabs/kuber-test-demo:latest
```
> `k8s/deployment.yaml` da `image` ni push qilingan manzilga moslashtiring.

### K8s manifestlarni apply:
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

### Tekshirish:
```bash
kubectl get pods
kubectl get svc
kubectl get ingress
kubectl get certificate
```

---

## 5. ArgoCD bilan (avtomatik sync)

`argocd/application.yaml` da `repoURL` ni o'z Git repo manzilingizga o'zgartiring:
```yaml
repoURL: https://github.com/YOUR_ORG/kuberTestDemo.git
```

Keyin:
```bash
kubectl apply -f argocd/application.yaml
```

ArgoCD `k8s/` papkasidagi barcha manifestlarni avtomatik sync qiladi.

---

## 6. HTTPS tekshirish

```bash
# Tashqaridan HTTPS tekshirish:
curl -v https://demo.feruzlabs.dev/kube-test-1/swagger-ui.html

# Sertifikat ma'lumotlari:
curl -vI https://demo.feruzlabs.dev/kube-test-1/
```

Brauzerda: `https://demo.feruzlabs.dev/kube-test-1/swagger-ui.html`

---

## Muammolar

### HSTS xatosi (Chrome "Proceed" bermasa)
```
chrome://net-internals/#hsts
```
"Delete domain security policies" → `demo.feruzlabs.dev` → Delete.

### Sertifikat tayyor bo'lmasa
```bash
kubectl describe certificate demo-feruzlabs-dev-tls
kubectl describe certificaterequest -n default
kubectl logs -n cert-manager deploy/cert-manager
```

### Pod ishlamasa
```bash
kubectl describe pod -l app=kuber-test-demo
kubectl logs -l app=kuber-test-demo
```
