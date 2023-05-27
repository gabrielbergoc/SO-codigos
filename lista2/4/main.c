#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pthread.h>
#include <math.h>
#include <string.h>


typedef struct {
    int x;
    bool xIsPrime;
} thread_data;


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

void *thread(void * args) {
    thread_data *data = (thread_data *) args;
    data->xIsPrime = isPrime(data->x);
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
    thread_data **data = (thread_data **) malloc(sizeof(thread_data *) * nThreads);
    for (int i = 0; i < nThreads; i++) {
        data[i] = (thread_data *) malloc(sizeof(thread_data));
        data[i]->x = atoi(argv[i + 1]);

        int created = pthread_create(&threads[i], NULL, thread, data[i]);

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

        char xIsPrime[20];
        if (data[i]->xIsPrime) {
            strcpy(xIsPrime, "is prime");
        } else {
            strcpy(xIsPrime, "is not prime");
        }

        printf("%d %s\n", i, xIsPrime);
    }


    free(threads);
    free(data);
}
