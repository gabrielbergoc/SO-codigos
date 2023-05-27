#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pthread.h>
#include <math.h>
#include <string.h>


int isPrime(int x)
{
    if (x < 2)
    {
        return false;
    }

    for (int i = 2; i <= sqrt(x); i++)
    {
        if (x % i == 0)
        {
            return false;
        }
    }

    return true;
}


char isPrimeStr[] = "is prime";
char isNotPrimeStr[] = "is not prime";

void *thread(void * args) {
    int *x = (int *) args;
    
    bool xIsPrime = isPrime(*x);

    printf("%d %s\n", *x, xIsPrime ? isPrimeStr : isNotPrimeStr);

    return NULL;
}

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        printf("Usage: main <x>\n");
        exit(0);
    }

    int nThreads = argc - 1;
    pthread_t *threads = (pthread_t *) malloc(sizeof(pthread_t) * nThreads);
    int **nums = (int **) malloc(sizeof(int *) * nThreads);
    for (int i = 0; i < nThreads; i++) {
        nums[i] = (int *) malloc(sizeof(int));
        *nums[i] = atoi(argv[i + 1]);

        int created = pthread_create(&threads[i], NULL, thread, nums[i]);

        if (created != 0) {
            printf("Error creating thread #%d\n", i);
            exit(created);
        }
    }

    for (int i = 0; i < nThreads; i++) {
        int joined = pthread_join(threads[i], NULL);

        if (joined != 0) {
            printf("Error joining thread #%d\n", i);
            exit(joined);
        }

        free(nums[i]);
    }

    free(threads);
    free(nums);
}
