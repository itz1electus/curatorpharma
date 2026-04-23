function pretty(elId, data) {
    document.getElementById(elId).textContent = JSON.stringify(data, null, 2);
}

async function loadCatalog() {
    const res = await fetch("/api/v1/catalog");
    pretty("catalogOutput", await res.json());
}

async function loadProduct() {
    const productId = document.getElementById("productIdInput").value;
    const res = await fetch(`/api/v1/catalog/products/${productId}`);
    pretty("productOutput", await res.json());
}

async function addCartItem() {
    const payload = {
        productId: document.getElementById("addProductId").value,
        quantity: Number(document.getElementById("addQuantity").value),
        unitOfMeasure: document.getElementById("addUnit").value
    };

    const res = await fetch("/api/v1/cart/items", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    pretty("cartOutput", await res.json());
}

async function loadCart() {
    const res = await fetch("/api/v1/cart");
    const data = await res.json();
    pretty("cartOutput", data);

    if (data.items && data.items.length > 0) {
        document.getElementById("updateItemId").value = data.items[0].itemId;
        document.getElementById("deleteItemId").value = data.items[0].itemId;
    }
}

async function updateCartItem() {
    const itemId = document.getElementById("updateItemId").value;
    const quantity = Number(document.getElementById("updateQuantity").value);

    const res = await fetch(`/api/v1/cart/items/${itemId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ quantity })
    });

    pretty("cartOutput", await res.json());
}

async function deleteCartItem() {
    const itemId = document.getElementById("deleteItemId").value;

    const res = await fetch(`/api/v1/cart/items/${itemId}`, {
        method: "DELETE"
    });

    if (res.status === 204) {
        loadCart();
    } else {
        pretty("cartOutput", { error: "Delete failed" });
    }
}

async function checkout() {
    const res = await fetch("/api/v1/cart/checkout", {
        method: "POST"
    });
    const data = await res.json();
    pretty("checkoutOutput", data);

    if (data.orderId) {
        document.getElementById("orderIdInput").value = data.orderId;
    }

    await loadCart();
}

async function loadOrder() {
    const orderId = document.getElementById("orderIdInput").value;
    const res = await fetch(`/api/v1/orders/${orderId}`);
    pretty("orderOutput", await res.json());
}