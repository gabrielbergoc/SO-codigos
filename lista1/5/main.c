#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <windows.h>
#include <pthread.h>

typedef int T;

typedef struct
{
    T *vector;
    int size;
} vector;

typedef struct
{
    vector *data;
    T target;
    int dir;
    int out;
} thread_data;

int find_element(T *vector, int size, T target, int dir)
{
    for (int i = 0; i < size; i++)
    {
        if (vector[(size + i*dir) % size] == target)
        {
            return i;
        }
    }
    return -1;
}

int randint(int min, int max)
{
    return rand() % (max - min + 1) + min;
}

void *thread(void *arg)
{
    thread_data *data = (thread_data *)arg;
    // if (data->dir > 0)
    // {
    //     Sleep(randint(0, 10));
    // }
    vector *vec = data->data;
    data->out = find_element(vec->vector, vec->size, data->target, data->dir);
    return NULL;
}

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        printf("Usage: ./main <target> <num1> <num2> ... <numN>\n");
        exit(0);
    }

    T target = atoi(argv[1]);

    srand(time(NULL));

    vector vec;
    vec.size = argc - 2;
    vec.vector = (T *)malloc((vec.size) * sizeof(T));
    for (int i = 0; i < vec.size; i++)
    {
        vec.vector[i] = atoi(argv[i + 2]);
    }

    thread_data data1;
    thread_data data2;
    data1.data = &vec;
    data2.data = &vec;
    data1.target = target;
    data2.target = target;
    data1.dir = 1;
    data2.dir = -1;

    pthread_t t1;
    pthread_t t2;

    pthread_create(&t1, NULL, thread, &data1);
    pthread_create(&t2, NULL, thread, &data2);

    int finished = 0;
    while (finished < 2)
    {
        if (_pthread_tryjoin(t1, NULL) == 0)
        {
            if (data1.out != -1)
            {
                printf("Found %d at index %d by thread 1\n", target, data1.out);
            }
            else
            {
                printf("Thread 1 did not find %d\n", target);
            }
            finished += 1;
        }
        else if (_pthread_tryjoin(t2, NULL) == 0)
        {
            if (data2.out != -1)
            {
                printf("Found %d at index %d by thread 2\n", target, vec.size - data2.out);
            }
            else
            {
                printf("Thread 2 did not find %d\n", target);
            }
            finished += 1;
        }
    }
}