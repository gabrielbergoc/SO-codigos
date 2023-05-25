#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef struct {
    int *vet;
    int n;
} thread_data;


void print_vet(int *vet, int n)
{
    for (int i = 0; i < n; i++)
    {
        printf("%d ", vet[i]);
    }
    printf("\n");
}

int comp(const void *a, const void *b)
{
    return (*(int *)a - *(int *)b);
}

double time_qsort(int *vet, int n)
{
    clock_t c1 = clock();
    qsort(vet, n, sizeof(int), comp);
    clock_t c2 = clock();
    return (c2 - c1) * 1000 / CLOCKS_PER_SEC;
}

void *thread_quick_sort(void *arg)
{
    thread_data *data = (thread_data *)arg;
    double time_quick = time_qsort(data->vet, data->n);
    printf("Tempo de execução do quick sort: %lf\n", time_quick);
    // printf("Resultado do quick sort: ");
    // print_vet(data->vet, data->n);
    return NULL;
}

void bubble_sort(int *vet, int n)
{
    for (int i = 0; i < n - 1; i++)
    {
        for (int j = 0; j < n - 1 - i; j++)
        {
            if (vet[j] > vet[j + 1])
            {
                int aux = vet[j];
                vet[j] = vet[j + 1];
                vet[j + 1] = aux;
            }
        }
    }
}

double time_bubble(int *vet, int n)
{
    clock_t c1 = clock();
    bubble_sort(vet, n);
    clock_t c2 = clock();
    return (c2 - c1) * 1000 / CLOCKS_PER_SEC;
}

void *thread_bubble_sort(void *arg)
{
    thread_data *data = (thread_data *)arg;
    double time_bubble_sort = time_bubble(data->vet, data->n);
    printf("Tempo de execução do bubble sort: %lf\n", time_bubble_sort);
    // printf("Resultado do bubble sort: ");
    // print_vet(data->vet, data->n);
    return NULL;
}

int main(int argv, char **argc)
{
    if (argv < 2)
    {
        printf("Usage: ./main <num1> <num2> ... <numN>\n");
        return 1;
    }

    int *vet1 = malloc(sizeof(int) * (argv - 1));
    int *vet2 = malloc(sizeof(int) * (argv - 1));
    for (int i = 0; i < argv - 1; i++)
    {
        vet1[i] = atoi(argc[i + 1]);
        vet2[i] = atoi(argc[i + 1]);
    }

    thread_data data1;
    data1.vet = vet1;
    data1.n = argv - 1;
    thread_data data2;
    data2.vet = vet2;
    data2.n = argv - 1;

    pthread_t thread1, thread2;
    int created2 = pthread_create(&thread2, NULL, thread_bubble_sort, (void *)&data2);
    if (created2 != 0)
    {
        printf("Erro ao criar thread\n");
        exit(created2);
    }
    int created1 = pthread_create(&thread1, NULL, thread_quick_sort, (void *)&data1);
    if (created1 != 0)
    {
        printf("Erro ao criar thread\n");
        exit(created1);
    }

    int joined1 = pthread_join(thread1, NULL);
    if (joined1 != 0)
    {
        printf("Erro ao esperar thread\n");
        exit(joined1);
    }
    int joined2 = pthread_join(thread2, NULL);
    if (joined2 != 0)
    {
        printf("Erro ao esperar thread\n");
        exit(joined2);
    }

    free(vet1);
    free(vet2);
    exit(0);
}
