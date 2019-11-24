const waterfall = require("async/waterfall");

function printAsync(string, cb) {
  const delay = Math.floor(Math.random() * 250 + 100);

  setTimeout(() => {
    console.log(string);

    if (cb) {
      cb();
    }
  }, delay);
}

function task1(cb) {
  printAsync("1", () => task2(cb));
}

function task2(cb) {
  printAsync("2", () => task3(cb));
}

function task3(cb) {
  printAsync("3", cb);
}

function loop(n, cb) {
  if (n > 0) {
    task1(() => loop(n - 1, cb));
  } else {
    if (cb) {
      cb();
    }
  }
}

let inparallelCounter = 0;
function inparallel(tasks, finalTask, counter = 0) {
  if (counter === tasks.length) {
    return;
  }

  tasks[counter](() => {
    inparallelCounter += 1;
    if (inparallelCounter === tasks.length) {
      finalTask();
    }
  });

  inparallel(tasks, finalTask, counter + 1);
}

function exc1a() {
  console.log();
  console.log("exc1a");

  loop(5, () => {
    console.log("done!");
    exc1b();
  });
}

function exc1b() {
  console.log();
  console.log("exc1b");

  let tasks = [];

  for (let i = 0; i < 5; ++i) {
    tasks.push(task1);
  }

  waterfall(tasks, () => {
    console.log("done!");
    exc2a();
  });
}

function exc2a() {
  console.log();
  console.log("exc2a");

  const a = cb => printAsync("A", cb);
  const b = cb => printAsync("B", cb);
  const c = cb => printAsync("C", cb);
  const d = cb => printAsync("done!", cb);

  inparallel([a, b, c], d);
}

exc1a();
