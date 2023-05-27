from random import randint, seed
import argparse

def gen_nums(n=10, min=0, max=100):
    return (randint(min, max) for _ in range(n))

parser = argparse.ArgumentParser(
    prog='nums_gen.py',
    description='Generate random numbers',
    epilog='Enjoy the program! :)',
)

parser.add_argument('-n', '--number', type=int, default=10, help='Number of random numbers')
parser.add_argument('-min', '--minimum', type=int, default=0, help='Minimum value')
parser.add_argument('-max', '--maximum', type=int, default=100, help='Maximum value')
parser.add_argument('-s', '--seed', type=int, default=None, help='Seed for random numbers')

args = parser.parse_args()

seed(args.seed)
nums = gen_nums(args.number, args.minimum, args.maximum)
print(*nums, sep=' ')
