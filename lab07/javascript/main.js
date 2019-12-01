const { performance } = require("perf_hooks");
const fs = require("fs");

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

  async acquireWith(other) {
    let wait = 1;

    while (true) {
      await timeout(wait);

      if (this.taken === false && other.taken === false) {
        this.taken = true;
        other.taken = true;
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

    this.timesAsym = [];
    this.timesConductor = [];
    this.timesBoth = [];

    return this;
  }

  async startNaive(count) {
    const forks = this.forks;
    const f1 = this.f1;
    const f2 = this.f2;
    const id = this.id;

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
      const start = performance.now();

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

      this.timesAsym.push(performance.now() - start);

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
      const start = performance.now();

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

      this.timesConductor.push(performance.now() - start);

      console.log(`Philosopher ${id} is eating...`);
      await timeout(Math.random() * 1000);

      forks[f1].release();
      forks[f2].release();

      conductor.unlock();

      console.log(`Philosopher ${id} finished eating.`);
      await timeout(Math.random() * 1000);
    }
  }

  async startBoth(count) {
    const forks = this.forks;
    const f1 = this.f1;
    const f2 = this.f2;
    const id = this.id;

    for (let i = 0; i < count; ++i) {
      const start = performance.now();

      console.log(`Philosopher ${id} awaiting both forks...`);
      await forks[f1].acquireWith(forks[f2]);

      this.timesBoth.push(performance.now() - start);

      console.log(`Philosopher ${id} is eating...`);
      await timeout(Math.random() * 1000);

      forks[f1].release();
      forks[f2].release();

      console.log(`Philosopher ${id} finished eating.`);
      await timeout(Math.random() * 1000);
    }
  }
}

async function runNaive(philosophers, iterations) {
  console.log("\n\nNaive version");
  let promises = [];

  for (let p of philosophers) {
    promises.push(p.startNaive(iterations));
  }

  for (let p of promises) {
    await p;
  }
}

async function runAsym(philosophers, iterations) {
  console.log("\n\nAsymmetrical version");

  let promises = [];
  for (let p of philosophers) {
    promises.push(p.startAsym(iterations));
  }

  for (let p of promises) {
    await p;
  }
}

async function runConductor(philosophers, n, iterations) {
  console.log("\n\nConductor version");

  let promises = [];
  let conductor = new Conductor(n);
  for (let p of philosophers) {
    promises.push(p.startConductor(iterations, conductor));
  }

  for (let p of promises) {
    await p;
  }
}

async function runBoth(philosophers, iterations) {
  console.log("\n\nBoth at once version");

  let promises = [];
  for (let p of philosophers) {
    promises.push(p.startBoth(iterations));
  }

  for (let p of promises) {
    await p;
  }
}

async function run() {
  const iterations = 5;

  for (let n of [5, 20, 100]) {
    let forks = [];
    let philosophers = [];

    for (let i = 0; i < n; ++i) {
      forks.push(new Fork());
    }

    for (let i = 0; i < n; ++i) {
      philosophers.push(new Philosopher(i, forks));
    }

    await runConductor(philosophers, n, iterations);
    await runAsym(philosophers, iterations);
    await runBoth(philosophers, iterations);

    let timesAsym = [];
    let timesConductor = [];
    let timesBoth = [];

    for (let p of philosophers) {
      timesAsym.push(...p.timesAsym);
      timesConductor.push(...p.timesConductor);
      timesBoth.push(...p.timesBoth);
    }

    const avg = arr => arr.reduce((a, b) => a + b, 0) / arr.length;

    let results = `
      Asym: ${avg(timesAsym)}
      Conductor: ${avg(timesConductor)}
      Both: ${avg(timesBoth)}
    `;

    console.info("Results:");
    console.log(results);

    fs.writeFile(`philosophers_${n}.txt`, results, err => {
      if (err) {
        console.error(err);
      }
    });
  }
}

run();
