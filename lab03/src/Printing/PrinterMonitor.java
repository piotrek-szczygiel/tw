package Printing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PrinterMonitor {
    private final Lock lock = new ReentrantLock();
    private int printers;

    private List<Printer> printerList = new ArrayList<>();

    PrinterMonitor(int printers) {
        this.printers = printers;

        for (int i = 0; i < printers; ++i) {
            Condition c = lock.newCondition();
            Printer printer = new Printer(i, c);
            printerList.add(printer);
        }
    }

    Printer reserve() throws InterruptedException {
        int printerIndex = (int) (Math.random() * printers);
        Printer printer = printerList.get(printerIndex);

        lock.lock();

        while (!printer.free()) {
            printer.await();
        }

        printer.occupy();

        lock.unlock();
        return printer;
    }

    void release(Printer printer) {
        lock.lock();
        printer.leave();
        printer.signal();
        lock.unlock();
    }
}
