#include <iostream>
using namespace std;
int main()
{
	srand(time(0));
	int a1, a2, summa = 0;
		a1 = rand() % 101 - 30;
		a2 = rand() % 101 - 30;
		while (a1 <= a2) {
			cout << a2 << " ";
			summa += a1;
			a1 = a2;
			a2 = rand() % 101 - 30;
		}
		cout << a2;
		cout << endl << summa;
}
