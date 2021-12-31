package com.sparkappdesign.archimedes.tommath;
/* loaded from: classes.dex */
public class tommathJNI {
    public static final native int C_MP_DIGIT_BIT_get();

    public static final native int mp_2expt(long j, int i);

    public static final native int mp_abs(long j, long j2);

    public static final native int mp_add(long j, long j2, long j3);

    public static final native int mp_add_d(long j, long j2, long j3);

    public static final native int mp_addmod(long j, long j2, long j3, long j4);

    public static final native long mp_alloc_init();

    public static final native int mp_and(long j, long j2, long j3);

    public static final native void mp_clamp(long j);

    public static final native void mp_clear(long j);

    public static final native void mp_clear_free(long j);

    public static final native void mp_clear_multi(long j);

    public static final native int mp_cmp(long j, long j2);

    public static final native int mp_cmp_d(long j, long j2);

    public static final native int mp_cmp_mag(long j, long j2);

    public static final native int mp_cnt_lsb(long j);

    public static final native int mp_copy(long j, long j2);

    public static final native int mp_count_bits(long j);

    public static final native int mp_div(long j, long j2, long j3, long j4);

    public static final native int mp_div_2(long j, long j2);

    public static final native int mp_div_2d(long j, int i, long j2, long j3);

    public static final native int mp_div_3(long j, long j2, long[] jArr);

    public static final native int mp_div_d(long j, long j2, long j3, long[] jArr);

    public static final native int mp_dr_is_modulus(long j);

    public static final native void mp_dr_setup(long j, long[] jArr);

    public static final native String mp_error_to_string(int i);

    public static final native void mp_exch(long j, long j2);

    public static final native int mp_expt_d(long j, long j2, long j3);

    public static final native int mp_exptmod(long j, long j2, long j3, long j4);

    public static final native int mp_exteuclid(long j, long j2, long j3, long j4, long j5);

    public static final native int mp_gcd(long j, long j2, long j3);

    public static final native long mp_get_digit(long j, int i);

    public static final native long mp_get_int(long j);

    public static final native int mp_get_sign(long j);

    public static final native String mp_get_str(long j, int i);

    public static final native int mp_get_used(long j);

    public static final native int mp_grow(long j, int i);

    public static final native int mp_init(long j);

    public static final native int mp_init_copy(long j, long j2);

    public static final native int mp_init_multi(long j);

    public static final native int mp_init_set(long j, long j2);

    public static final native int mp_init_set_int(long j, long j2);

    public static final native int mp_init_size(long j, int i);

    public static final native int mp_invmod(long j, long j2, long j3);

    public static final native int mp_is_square(long j, int[] iArr);

    public static final native int mp_jacobi(long j, long j2, int[] iArr);

    public static final native int mp_lcm(long j, long j2, long j3);

    public static final native int mp_lshd(long j, int i);

    public static final native int mp_mod(long j, long j2, long j3);

    public static final native int mp_mod_2d(long j, int i, long j2);

    public static final native int mp_mod_d(long j, long j2, long[] jArr);

    public static final native int mp_montgomery_calc_normalization(long j, long j2);

    public static final native int mp_montgomery_reduce(long j, long j2, long j3);

    public static final native int mp_montgomery_setup(long j, long[] jArr);

    public static final native int mp_mul(long j, long j2, long j3);

    public static final native int mp_mul_2(long j, long j2);

    public static final native int mp_mul_2d(long j, int i, long j2);

    public static final native int mp_mul_d(long j, long j2, long j3);

    public static final native int mp_mulmod(long j, long j2, long j3, long j4);

    public static final native int mp_n_root(long j, long j2, long j3);

    public static final native int mp_neg(long j, long j2);

    public static final native int mp_or(long j, long j2, long j3);

    public static final native int mp_prime_fermat(long j, long j2, int[] iArr);

    public static final native int mp_prime_is_divisible(long j, int[] iArr);

    public static final native int mp_prime_is_prime(long j, int i, int[] iArr);

    public static final native int mp_prime_miller_rabin(long j, long j2, int[] iArr);

    public static final native int mp_prime_next_prime(long j, int i, int i2);

    public static final native int mp_prime_rabin_miller_trials(int i);

    public static final native int mp_rand(long j, int i);

    public static final native int mp_read_radix(long j, String str, int i);

    public static final native int mp_reduce(long j, long j2, long j3);

    public static final native int mp_reduce_2k(long j, long j2, long j3);

    public static final native int mp_reduce_2k_l(long j, long j2, long j3);

    public static final native int mp_reduce_2k_setup(long j, long[] jArr);

    public static final native int mp_reduce_2k_setup_l(long j, long j2);

    public static final native int mp_reduce_is_2k(long j);

    public static final native int mp_reduce_is_2k_l(long j);

    public static final native int mp_reduce_setup(long j, long j2);

    public static final native void mp_rshd(long j, int i);

    public static final native void mp_set(long j, long j2);

    public static final native void mp_set_digit(long j, int i, long j2);

    public static final native int mp_set_int(long j, long j2);

    public static final native void mp_set_sign(long j, int i);

    public static final native void mp_set_used(long j, int i);

    public static final native int mp_shrink(long j);

    public static final native int mp_sqr(long j, long j2);

    public static final native int mp_sqrmod(long j, long j2, long j3);

    public static final native int mp_sqrt(long j, long j2);

    public static final native int mp_sub(long j, long j2, long j3);

    public static final native int mp_sub_d(long j, long j2, long j3);

    public static final native int mp_submod(long j, long j2, long j3, long j4);

    public static final native int mp_xor(long j, long j2, long j3);

    public static final native void mp_zero(long j);
}
