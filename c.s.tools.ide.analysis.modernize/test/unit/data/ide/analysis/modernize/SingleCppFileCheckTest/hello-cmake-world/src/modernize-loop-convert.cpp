#include <vector>
#include <iostream>

using namespace std;

int loops() {
    const int N = 5;
    int arr[] = {1, 2, 3, 4, 5};
    vector<int> v;
    v.push_back(1);
    v.push_back(2);
    v.push_back(3);

    // safe conversion
    for (int i = 0; i < N; ++i)
        cout << arr[i];
 
    // reasonable conversion
    for (vector<int>::iterator it = v.begin(); it != v.end(); ++it)
        cout << *it;

    // reasonable conversion
    for (int i = 0; i < v.size(); ++i)
        cout << v[i];
}    