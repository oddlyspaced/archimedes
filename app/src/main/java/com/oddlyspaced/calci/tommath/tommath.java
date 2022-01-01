package com.oddlyspaced.calci.tommath;
/* loaded from: classes.dex */
public class tommath implements tommathConstants {
    public static long mp_alloc_init() {
        return tommathJNI.mp_alloc_init();
    }

    public static void mp_clear_free(SWIGTYPE_p_mp_int value) {
        tommathJNI.mp_clear_free(SWIGTYPE_p_mp_int.getCPtr(value));
    }

    public static int getC_MP_DIGIT_BIT() {
        return tommathJNI.C_MP_DIGIT_BIT_get();
    }

    public static int mp_get_sign(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_get_sign(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_get_used(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_get_used(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static long mp_get_digit(SWIGTYPE_p_mp_int a, int i) {
        return tommathJNI.mp_get_digit(SWIGTYPE_p_mp_int.getCPtr(a), i);
    }

    public static void mp_set_sign(SWIGTYPE_p_mp_int a, int sign) {
        tommathJNI.mp_set_sign(SWIGTYPE_p_mp_int.getCPtr(a), sign);
    }

    public static void mp_set_used(SWIGTYPE_p_mp_int a, int used) {
        tommathJNI.mp_set_used(SWIGTYPE_p_mp_int.getCPtr(a), used);
    }

    public static void mp_set_digit(SWIGTYPE_p_mp_int a, int i, long digit) {
        tommathJNI.mp_set_digit(SWIGTYPE_p_mp_int.getCPtr(a), i, digit);
    }

    public static String mp_get_str(SWIGTYPE_p_mp_int a, int radix) {
        return tommathJNI.mp_get_str(SWIGTYPE_p_mp_int.getCPtr(a), radix);
    }

    public static String mp_error_to_string(int code) {
        return tommathJNI.mp_error_to_string(code);
    }

    public static int mp_2expt(SWIGTYPE_p_mp_int a, int b) {
        return tommathJNI.mp_2expt(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_abs(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_abs(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_add(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_add(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_add_d(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_add_d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_addmod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_addmod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_and(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_and(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static void mp_clamp(SWIGTYPE_p_mp_int a) {
        tommathJNI.mp_clamp(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static void mp_clear(SWIGTYPE_p_mp_int a) {
        tommathJNI.mp_clear(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static void mp_clear_multi(SWIGTYPE_p_mp_int mp) {
        tommathJNI.mp_clear_multi(SWIGTYPE_p_mp_int.getCPtr(mp));
    }

    public static int mp_cmp(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_cmp(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_cmp_d(SWIGTYPE_p_mp_int a, long b) {
        return tommathJNI.mp_cmp_d(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_cmp_mag(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_cmp_mag(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_cnt_lsb(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_cnt_lsb(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_copy(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_copy(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_count_bits(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_count_bits(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_div(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_div(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_div_2(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_div_2(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_div_2d(SWIGTYPE_p_mp_int a, int b, SWIGTYPE_p_mp_int c, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_div_2d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_div_3(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int c, long[] d) {
        return tommathJNI.mp_div_3(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(c), d);
    }

    public static int mp_div_d(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c, long[] d) {
        return tommathJNI.mp_div_d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c), d);
    }

    public static int mp_dr_is_modulus(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_dr_is_modulus(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static void mp_dr_setup(SWIGTYPE_p_mp_int a, long[] d) {
        tommathJNI.mp_dr_setup(SWIGTYPE_p_mp_int.getCPtr(a), d);
    }

    public static void mp_exch(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        tommathJNI.mp_exch(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_expt_d(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_expt_d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_exptmod(SWIGTYPE_p_mp_int G, SWIGTYPE_p_mp_int X, SWIGTYPE_p_mp_int P, SWIGTYPE_p_mp_int Y) {
        return tommathJNI.mp_exptmod(SWIGTYPE_p_mp_int.getCPtr(G), SWIGTYPE_p_mp_int.getCPtr(X), SWIGTYPE_p_mp_int.getCPtr(P), SWIGTYPE_p_mp_int.getCPtr(Y));
    }

    public static int mp_exteuclid(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int U1, SWIGTYPE_p_mp_int U2, SWIGTYPE_p_mp_int U3) {
        return tommathJNI.mp_exteuclid(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(U1), SWIGTYPE_p_mp_int.getCPtr(U2), SWIGTYPE_p_mp_int.getCPtr(U3));
    }

    public static int mp_gcd(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_gcd(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static long mp_get_int(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_get_int(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_grow(SWIGTYPE_p_mp_int a, int size) {
        return tommathJNI.mp_grow(SWIGTYPE_p_mp_int.getCPtr(a), size);
    }

    public static int mp_init(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_init(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_init_copy(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_init_copy(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_init_multi(SWIGTYPE_p_mp_int mp) {
        return tommathJNI.mp_init_multi(SWIGTYPE_p_mp_int.getCPtr(mp));
    }

    public static int mp_init_set(SWIGTYPE_p_mp_int a, long b) {
        return tommathJNI.mp_init_set(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_init_set_int(SWIGTYPE_p_mp_int a, long b) {
        return tommathJNI.mp_init_set_int(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_init_size(SWIGTYPE_p_mp_int a, int size) {
        return tommathJNI.mp_init_size(SWIGTYPE_p_mp_int.getCPtr(a), size);
    }

    public static int mp_invmod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_invmod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_is_square(SWIGTYPE_p_mp_int arg, int[] ret) {
        return tommathJNI.mp_is_square(SWIGTYPE_p_mp_int.getCPtr(arg), ret);
    }

    public static int mp_jacobi(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int p, int[] c) {
        return tommathJNI.mp_jacobi(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(p), c);
    }

    public static int mp_lcm(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_lcm(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_lshd(SWIGTYPE_p_mp_int a, int b) {
        return tommathJNI.mp_lshd(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_mod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_mod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_mod_2d(SWIGTYPE_p_mp_int a, int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_mod_2d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_mod_d(SWIGTYPE_p_mp_int a, long b, long[] c) {
        return tommathJNI.mp_mod_d(SWIGTYPE_p_mp_int.getCPtr(a), b, c);
    }

    public static int mp_montgomery_calc_normalization(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_montgomery_calc_normalization(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_montgomery_reduce(SWIGTYPE_p_mp_int x, SWIGTYPE_p_mp_int n, long rho) {
        return tommathJNI.mp_montgomery_reduce(SWIGTYPE_p_mp_int.getCPtr(x), SWIGTYPE_p_mp_int.getCPtr(n), rho);
    }

    public static int mp_montgomery_setup(SWIGTYPE_p_mp_int n, long[] rho) {
        return tommathJNI.mp_montgomery_setup(SWIGTYPE_p_mp_int.getCPtr(n), rho);
    }

    public static int mp_mul(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_mul(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_mul_2(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_mul_2(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_mul_2d(SWIGTYPE_p_mp_int a, int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_mul_2d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_mul_d(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_mul_d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_mulmod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_mulmod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_n_root(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_n_root(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_neg(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_neg(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_or(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_or(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_prime_fermat(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, int[] OUTPUT) {
        return tommathJNI.mp_prime_fermat(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), OUTPUT);
    }

    public static int mp_prime_is_divisible(SWIGTYPE_p_mp_int a, int[] OUTPUT) {
        return tommathJNI.mp_prime_is_divisible(SWIGTYPE_p_mp_int.getCPtr(a), OUTPUT);
    }

    public static int mp_prime_is_prime(SWIGTYPE_p_mp_int a, int t, int[] OUTPUT) {
        return tommathJNI.mp_prime_is_prime(SWIGTYPE_p_mp_int.getCPtr(a), t, OUTPUT);
    }

    public static int mp_prime_miller_rabin(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, int[] OUTPUT) {
        return tommathJNI.mp_prime_miller_rabin(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), OUTPUT);
    }

    public static int mp_prime_next_prime(SWIGTYPE_p_mp_int a, int t, int bbs_style) {
        return tommathJNI.mp_prime_next_prime(SWIGTYPE_p_mp_int.getCPtr(a), t, bbs_style);
    }

    public static int mp_prime_rabin_miller_trials(int size) {
        return tommathJNI.mp_prime_rabin_miller_trials(size);
    }

    public static int mp_rand(SWIGTYPE_p_mp_int a, int digits) {
        return tommathJNI.mp_rand(SWIGTYPE_p_mp_int.getCPtr(a), digits);
    }

    public static int mp_read_radix(SWIGTYPE_p_mp_int a, String str, int radix) {
        return tommathJNI.mp_read_radix(SWIGTYPE_p_mp_int.getCPtr(a), str, radix);
    }

    public static int mp_reduce(SWIGTYPE_p_mp_int x, SWIGTYPE_p_mp_int m, SWIGTYPE_p_mp_int mu) {
        return tommathJNI.mp_reduce(SWIGTYPE_p_mp_int.getCPtr(x), SWIGTYPE_p_mp_int.getCPtr(m), SWIGTYPE_p_mp_int.getCPtr(mu));
    }

    public static int mp_reduce_2k(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int n, long d) {
        return tommathJNI.mp_reduce_2k(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(n), d);
    }

    public static int mp_reduce_2k_l(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int n, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_reduce_2k_l(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(n), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_reduce_2k_setup(SWIGTYPE_p_mp_int a, long[] d) {
        return tommathJNI.mp_reduce_2k_setup(SWIGTYPE_p_mp_int.getCPtr(a), d);
    }

    public static int mp_reduce_2k_setup_l(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_reduce_2k_setup_l(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_reduce_is_2k(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_reduce_is_2k(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_reduce_is_2k_l(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_reduce_is_2k_l(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_reduce_setup(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_reduce_setup(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static void mp_rshd(SWIGTYPE_p_mp_int a, int b) {
        tommathJNI.mp_rshd(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static void mp_set(SWIGTYPE_p_mp_int a, long b) {
        tommathJNI.mp_set(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_set_int(SWIGTYPE_p_mp_int a, long b) {
        return tommathJNI.mp_set_int(SWIGTYPE_p_mp_int.getCPtr(a), b);
    }

    public static int mp_shrink(SWIGTYPE_p_mp_int a) {
        return tommathJNI.mp_shrink(SWIGTYPE_p_mp_int.getCPtr(a));
    }

    public static int mp_sqr(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b) {
        return tommathJNI.mp_sqr(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b));
    }

    public static int mp_sqrmod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_sqrmod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_sqrt(SWIGTYPE_p_mp_int arg, SWIGTYPE_p_mp_int ret) {
        return tommathJNI.mp_sqrt(SWIGTYPE_p_mp_int.getCPtr(arg), SWIGTYPE_p_mp_int.getCPtr(ret));
    }

    public static int mp_sub(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_sub(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_sub_d(SWIGTYPE_p_mp_int a, long b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_sub_d(SWIGTYPE_p_mp_int.getCPtr(a), b, SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static int mp_submod(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c, SWIGTYPE_p_mp_int d) {
        return tommathJNI.mp_submod(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c), SWIGTYPE_p_mp_int.getCPtr(d));
    }

    public static int mp_xor(SWIGTYPE_p_mp_int a, SWIGTYPE_p_mp_int b, SWIGTYPE_p_mp_int c) {
        return tommathJNI.mp_xor(SWIGTYPE_p_mp_int.getCPtr(a), SWIGTYPE_p_mp_int.getCPtr(b), SWIGTYPE_p_mp_int.getCPtr(c));
    }

    public static void mp_zero(SWIGTYPE_p_mp_int a) {
        tommathJNI.mp_zero(SWIGTYPE_p_mp_int.getCPtr(a));
    }
}
