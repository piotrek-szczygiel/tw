public class Client implements Runnable {
    private Shop shop;
    private int id;

    Client(Shop shop, int id) {
        this.shop = shop;
        this.id = id;
    }

    @Override
    public void run() {
        Cart cart = shop.takeCart();
        System.out.println("Client " + id + " took cart " + cart.getId());

        try {
            Thread.sleep((int) (Math.random() * 2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Client " + id + " returned cart " + cart.getId());
        shop.returnCart(cart);
    }
}
