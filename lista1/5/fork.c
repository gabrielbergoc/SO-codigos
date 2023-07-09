#include <stdio.h>
#include <unistd.h>
#include <time.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>

typedef struct {
    int *vec;
    int size;
} vector;

int find_element(vector *v, int target, int dir) {
    for (int i = 0; i < v->size; i++) {
        if (v->vec[(v->size + i*dir) % v->size] == target) {
            return (v->size + i*dir) % v->size;
        }
    }

    return -1;
}

void print_vector(vector *v) {
    for (int i = 0; i < v->size; i++) {
        printf("%d ", v->vec[i]);
    }
    printf("\n");
}

int comp(const void *a, const void *b) {
    return *(int *)a - *(int *)b;
}

int has_duplicated(vector *v) {
    vector copy;
    copy.vec = malloc(sizeof(int) * v->size);
    copy.size = v->size;
    memcpy(copy.vec, v->vec, sizeof(int) * v->size);
    qsort(copy.vec, copy.size, sizeof(int), comp);
    int prev = -1;
    int has_dupl = 0;
    for (int i = 0; i < v->size; i++) {
        // printf("%d ", copy.vec[i]);
        if (copy.vec[i] == prev) {
            has_dupl = 1;
            break;
        }
        prev = copy.vec[i];
    }

    free(copy.vec);

    return has_dupl;
}

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("Usage: ./main <array_size>\n");
        printf("max array_size = 256\n");
        exit(0);
    }

    vector v;
    v.size = atoi(argv[1]);
    if (v.size < 1 || v.size > 256) {
        printf("Usage: ./main <array_size>\n");
        printf("max array_size = 256\n");
        exit(0);
    }
    int *map = malloc(sizeof(int) * v.size);
    for (int i = 0; i < v.size; i++) {
        map[i] = 0;
    }
    v.vec = malloc(sizeof(int) * v.size);
    if (v.vec == NULL) {
        printf("Erro ao alocar memoria\n");
        exit(1);
    }

    srand(time(NULL));
    for (int i = 0; i < v.size; i++) {
        int x;
        do {
            x = rand() % v.size;
        } while(map[x]);
        map[x] = 1;

        v.vec[i] = x;
    }

    // int has = has_duplicated(&v);
    // printf("Tem duplicados: %d\n", has);

    int pid = 0;
    pid = fork();
    int find = rand() % v.size;
    int found;

    if (pid == 0) {
        found = find_element(&v, find, -1);
        printf("Fim do processo filho\n");
        exit(found);
    } else if (pid > 0) {
        double waitFor = (double) (rand() % 1001) / 1000;
        double diff = sleep(waitFor);
        printf("Processo pai esperou %lf segundo\n", waitFor - diff);
        found = find_element(&v, find, 1);
    } else {
        printf("Erro ao criar processo filho\n");
        exit(1);
    }

    int status;
    waitpid(pid, &status, 0);
    int child_found = WEXITSTATUS(status);

    printf("Processo pai encontrou o elemento %d na posição %d\n", find, found);
    printf("Processo filho encontrou o elemento %d na posição %d\n", find, child_found);      

    free(v.vec);
    free(map);
    exit(0);
}