import threading
import time
import random


class Fork:
    def __init__(self):
        self.lock = threading.Lock()

    def acquire(self):
        self.lock.acquire()

    def release(self):
        self.lock.release()

    def tryAcquire(self) -> bool:
        return self.lock.acquire(blocking=False)


class Conductor:
    def __init__(self, clients: int):
        self.semaphore = threading.BoundedSemaphore(value=clients - 1)

    def acquire(self):
        self.semaphore.acquire()

    def release(self):
        self.semaphore.release()


class Philosopher:
    def __init__(self, id: int, fork_left: Fork, fork_right: Fork):
        self.id = id
        self.fork_left = fork_left
        self.fork_right = fork_right

        self.times_asym = []
        self.times_conductor = []
        self.times_both = []

    def message(self, message: str):
        print(f"Philosopher {self.id}: {message}")

    def run_naive(self, iterations: int):
        for _ in range(iterations):
            self.message("awaiting left fork...")
            self.fork_left.acquire()

            self.message("awaiting right fork...")
            self.fork_right.acquire()

            self.message("is eating...")

            time.sleep(random.random())

            self.fork_left.release()
            self.fork_right.release()

            self.message("finished eating.")

            time.sleep(random.random())

    def run_asym(self, iterations: int):
        for _ in range(iterations):
            start = time.monotonic()

            if self.id % 2 == 0:
                self.message("awaiting left fork...")
                self.fork_left.acquire()

                self.message("awaiting right fork...")
                self.fork_right.acquire()
            else:
                self.message("awaiting right fork...")
                self.fork_right.acquire()

                self.message("awaiting left fork...")
                self.fork_left.acquire()

            self.times_asym.append(time.monotonic() - start)

            self.message("is eating...")

            time.sleep(random.random())

            self.fork_left.release()
            self.fork_right.release()

            self.message("finished eating.")

            time.sleep(random.random())

    def run_conductor(self, conductor: Conductor, iterations: int):
        for _ in range(iterations):
            start = time.monotonic()

            conductor.acquire()

            if self.id % 2 == 0:
                self.message("awaiting left fork...")
                self.fork_left.acquire()

                self.message("awaiting right fork...")
                self.fork_right.acquire()
            else:
                self.message("awaiting right fork...")
                self.fork_right.acquire()

                self.message("awaiting left fork...")
                self.fork_left.acquire()

            self.times_conductor.append(time.monotonic() - start)

            self.message("is eating...")

            time.sleep(random.random())

            self.fork_left.release()
            self.fork_right.release()
            conductor.release()

            self.message("finished eating.")

            time.sleep(random.random())

    def run_both(self, iterations: int):
        for _ in range(iterations):
            start = time.monotonic()

            self.message("awaiting both forks...")

            both = False
            while not both:
                self.fork_left.acquire()
                both = self.fork_right.tryAcquire()

                if not both:
                    self.fork_left.release()

            self.times_both.append(time.monotonic() - start)

            self.message("is eating...")

            time.sleep(random.random())

            self.fork_left.release()
            self.fork_right.release()

            self.message("finished eating.")

            time.sleep(random.random())


if __name__ == "__main__":
    random.seed()

    for n in [5, 20, 100]:
        conductor = Conductor(n)

        forks = []
        philosophers = []
        threads = []

        for i in range(n):
            forks.append(Fork())

        for i in range(n):
            philosophers.append(Philosopher(i, forks[i], forks[(i + 1) % n]))

        threads = []
        for i in range(n):
            threads.append(
                threading.Thread(
                    target=Philosopher.run_asym, args=(philosophers[i], 10)
                )
            )

        for t in threads:
            t.start()

        for t in threads:
            t.join()

        threads = []
        for i in range(n):
            threads.append(
                threading.Thread(
                    target=Philosopher.run_conductor,
                    args=(philosophers[i], conductor, 10),
                )
            )

        for t in threads:
            t.start()

        for t in threads:
            t.join()

        threads = []
        for i in range(n):
            threads.append(
                threading.Thread(
                    target=Philosopher.run_both, args=(philosophers[i], 10)
                )
            )

        for t in threads:
            t.start()

        for t in threads:
            t.join()

        all_asym = []
        all_conductor = []
        all_both = []

        for p in philosophers:
            all_asym.extend(p.times_asym)
            all_conductor.extend(p.times_conductor)
            all_both.extend(p.times_both)

        avg_asym = sum(all_asym) / len(all_asym)
        avg_conductor = sum(all_conductor) / len(all_conductor)
        avg_both = sum(all_both) / len(all_both)

        result = """
        Asym: {}
        Conductor: {}
        Both: {}
        """.format(
            avg_asym, avg_conductor, avg_both
        )

        print(result)

        with open(f"philosophers_{n}.txt", "w") as file:
            file.write(result)
