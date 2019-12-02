import random, semaphore, locks, strformat, os

type Philosopher = ref object
    id: int
    forkLeft: int
    forkRight: int


const n = 5

var
    forks: array[n, Lock]
    philosophers: array[n, Philosopher]
    conductor: Semaphore
    threads: array[n, Thread[Philosopher]]


proc message(p: Philosopher, message: string) =
    echo &"Philosopher {p.id}: {message}"

proc runNaive(p: Philosopher) {.thread.} =
    p.message "awaiting left fork..."
    acquire forks[p.forkLeft]

    p.message "awaiting right fork..."
    acquire forks[p.forkRight]

    p.message "is eating..."

    sleep rand(1000)

    release forks[p.forkLeft]
    release forks[p.forkRight]

    p.message "finished eating."

    sleep rand(1000)

proc runAsym(p: Philosopher) {.thread.} =
    if p.id mod 2 == 0:
        p.message "awaiting left fork..."
        acquire forks[p.forkLeft]

        p.message "awaiting right fork..."
        acquire forks[p.forkRight]
    else:
        p.message "awaiting right fork..."
        acquire forks[p.forkRight]

        p.message "awaiting left fork..."
        acquire forks[p.forkLeft]

    p.message "is eating..."

    sleep rand(1000)

    release forks[p.forkLeft]
    release forks[p.forkRight]

    p.message "finished eating."

    sleep rand(1000)

proc runConductor(p: Philosopher) {.thread.} =
    await conductor

    if p.id mod 2 == 0:
        p.message "awaiting left fork..."
        acquire forks[p.forkLeft]

        p.message "awaiting right fork..."
        acquire forks[p.forkRight]
    else:
        p.message "awaiting right fork..."
        acquire forks[p.forkRight]

        p.message "awaiting left fork..."
        acquire forks[p.forkLeft]

    p.message "is eating..."

    sleep rand(1000)

    release forks[p.forkLeft]
    release forks[p.forkRight]
    signal conductor

    p.message "finished eating."
    sleep rand(1000)

proc runBoth(p: Philosopher) {.thread.} =
    p.message "awaiting both forks..."

    var both = false
    while not both:
        acquire forks[p.forkLeft]
        both = tryAcquire forks[p.forkRight]

        if not both:
            release forks[p.forkLeft]

    p.message "is eating..."

    sleep rand(1000)

    release forks[p.forkLeft]
    release forks[p.forkRight]

    p.message "finished eating."

    sleep rand(1000)



when isMainModule:
    randomize()

    conductor = Semaphore(counter: n - 1)
    initSemaphore conductor

    for i in 0..<n:
        initLock forks[i]
        philosophers[i] = Philosopher(
            id: i,
            forkLeft: i,
            forkRight: (i + 1) mod n
        )

    # echo "\n\nNaive version"
    # for i in 0..<n:
    #     createThread(threads[i], runNaive, philosophers[i])

    # joinThreads(threads)

    echo "\n\nAsymmetric version"
    for i in 0..<n:
        createThread(threads[i], runAsym, philosophers[i])

    joinThreads(threads)

    echo "\n\nConductor version"
    for i in 0..<n:
        createThread(threads[i], runConductor, philosophers[i])

    joinThreads(threads)

    echo "\n\nBoth version"
    for i in 0..<n:
        createThread(threads[i], runBoth, philosophers[i])

    joinThreads(threads)
