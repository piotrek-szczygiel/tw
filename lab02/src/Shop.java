import java.util.Stack;

class Shop {
    private CountingSemaphore sem;
    private Stack<Cart> cartStack = new Stack<>();

    Shop(int num) {
        sem = new CountingSemaphore(num);
        for (int i = 0; i < num; ++i) {
            cartStack.push(new Cart(i));
        }
    }

    Cart takeCart() {
        sem.p();
        return cartStack.pop();
    }

    void returnCart(Cart cart) {
        cartStack.push(cart);
        sem.v();
    }
}
