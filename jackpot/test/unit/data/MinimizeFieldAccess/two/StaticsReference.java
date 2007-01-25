package two;

import one.PublicStatics;

class StaticsReference {
    int pass() { return PublicStatics.PASS; }
    int fail() { return PublicStatics.FAIL; }
}