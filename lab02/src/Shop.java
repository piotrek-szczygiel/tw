import java.util.Stack;

class Shop {
    private CountingSemaphore countingSem;
    private BinarySemaphore binarySem;
    private Stack<Cart> cartStack = new Stack<>();

    Shop(int num) {
        countingSem = new CountingSemaphore(num);
        binarySem = new BinarySemaphore();
        for (int i = 0; i < num; ++i) {
            cartStack.push(new Cart(i));
        }
    }

    Cart takeCart() {
        countingSem.p();
        binarySem.p();
        Cart cart = cartStack.pop();
        binarySem.v();
        return cart;
    }

    void returnCart(Cart cart) {
        binarySem.p();
        cartStack.push(cart);
        binarySem.v();
        countingSem.v();
    }
}
