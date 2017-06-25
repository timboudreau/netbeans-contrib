#include <vector>

using namespace std;

int loops() {
    const int N = 5;
    int arr[] = {1, 2, 3, 4, 5};
    vector<int> v;
    v.push_back(1);
    v.push_back(2);
    v.push_back(3);

    // safe conversion
    for (auto & elem : arr)
        cout << elem;

    // reasonable conversion
    for (auto & elem : v)
        cout << elem;

    // reasonable conversion
    for (auto & elem : v)
        cout << elem;
}