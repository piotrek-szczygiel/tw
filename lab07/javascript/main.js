function timeout(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

class Conductor {
  constructor(clients) {
    this.available = clients - 1;
  }

  async lock() {
    let wait = 1;

    while (true) {
      await timeout(wait);

      if (this.available > 0) {
        --this.available;
        break;
      }

      console.log("Conductor ordered philosopher to wait for his turn");
      wait *= 2;
    }
  }

  unlock() {
    ++this.available;
  }
}

class Fork {
  constructor() {
    this.taken = false;

    return this;
  }

  async acquire() {
    let wait = 1;

    while (true) {
      await timeout(wait);

      if (this.taken === false) {
        this.taken = true;
        break;
      }

      wait *= 2;
    }
  }

  release() {
    this.taken = false;
  }
}

class Philosopher {
  constructor(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id + 1) % forks.length;

    return this;
  }

  async startNaive(count) {
    const forks = this.forks,
      f1 = this.f1,
      f2 = this.f2,
      id = this.id;

    for (let i = 0; i < count; ++i) {
      console.log(`Philosopher ${id} awaiting first fork...`);
      await forks[f1].acquire();

      console.log(`Philosopher ${id} awaiting second fork...`);
      await forks[f2].acquire();

      console.log(`Philosopher ${id} is eating...`);
      await timeout(Math.random() * 1000);

      forks[f1].release();
      forks[f2].release();

      console.log(`Philosopher ${id} finished eating.`);
      await timeout(Math.random() * 1000);
    }
  }

  async startAsym(count) {
    const forks = this.forks;
    const f1 = this.f1;
    const f2 = this.f2;
    const id = this.id;

    for (let i = 0; i < count; ++i) {
      if (id % 2 === 0) {
        console.log(`Philosopher ${id} awaiting first fork...`);
        await forks[f1].acquire();

        console.log(`Philosopher ${id} awaiting second fork...`);
        await forks[f2].acquire();
      } else {
        console.log(`Philosopher ${id} awaiting second fork...`);
        await forks[f2].acquire();

        console.log(`Philosopher ${id} awaiting first fork...`);
        await forks[f1].acquire();
      }

      console.log(`Philosopher ${id} is eating...`);
      await timeout(Math.random() * 1000);

      forks[f1].release();
      forks[f2].release();

      console.log(`Philosopher ${id} finished eating.`);
      await timeout(Math.random() * 1000);
    }
  }

  async startConductor(count, conductor) {
    const forks = this.forks;
    const f1 = this.f1;
    const f2 = this.f2;
    const id = this.id;

    for (let i = 0; i < count; ++i) {
      await conductor.lock();

      if (id % 2 === 0) {
        console.log(`Philosopher ${id} awaiting first fork...`);
        await forks[f1].acquire();

        console.log(`Philosopher ${id} awaiting second fork...`);
        await forks[f2].acquire();
      } else {
        console.log(`Philosopher ${id} awaiting second fork...`);
        await forks[f2].acquire();

        console.log(`Philosopher ${id} awaiting first fork...`);
        await forks[f1].acquire();
      }

      console.log(`Philosopher ${id} is eating...`);
      await timeout(Math.random() * 1000);

      forks[f1].release();
      forks[f2].release();

      conductor.unlock();

      console.log(`Philosopher ${id} finished eating.`);
      await timeout(Math.random() * 1000);
    }
  }
}

function runNaive(philosophers) {
  console.log("\n\nNaive version");

  for (let p of philosophers) {
    p.startNaive(10);
  }
}

function runAsym(philosophers) {
  console.log("\n\nAsymmetrical version");

  for (let p of philosophers) {
    p.startAsym(10);
  }
}

function runConductor(philosophers) {
  console.log("\n\nConductor version");

  let conductor = new Conductor(N);
  for (let p of philosophers) {
    p.startConductor(10, conductor);
  }
}

const N = 5;
let forks = [];
let philosophers = [];

for (let i = 0; i < N; ++i) {
  forks.push(new Fork());
}

for (let i = 0; i < N; ++i) {
  philosophers.push(new Philosopher(i, forks));
}

// runNaive(philosophers);
// runAsym(philosophers);
// runConductor(philosophers);
