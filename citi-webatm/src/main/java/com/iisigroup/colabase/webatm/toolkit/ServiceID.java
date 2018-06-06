package com.iisigroup.colabase.webatm.toolkit;
/*
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Sun Community Source License
 * Jini(TM) Technology Core Platform, v.1.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of
 * the License at http://java.sun.com/products/jini. Software distributed
 * under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
 * ANY KIND, either express or implied.  See the License for the specific
 * language governing rights and limitations under the License.
 *
 * CopyrightVersion v1.0_Jini
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * A universally unique identifier (UUID) for registered services. A service ID is a 128-bit value. Service IDs are intended to be generated only by lookup services, not by clients.
 * <p>
 * The most significant long can be decomposed into the following unsigned fields: * 0xFFFFFFFF00000000 time_low 0x00000000FFFF0000 time_mid 0x000000000000F000 version 0x0000000000000FFF time_hi
 * <p>
 * The least significant long can be decomposed into the following unsigned fields: * 0xC000000000000000 variant 0x3FFF000000000000 clock_seq 0x0000FFFFFFFFFFFF node
 * <p>
 * The variant field must be 0x2. The version field must be either 0x1 or 0x4. If the version field is 0x4, then the most significant bit of the node field must be set to 1, and the remaining fields
 * are set to values produced by a cryptographically strong pseudo-random number generator. If the version field is 0x1, then the node field is set to an IEEE 802 address, the clock_seq field is set
 * to a 14-bit random number, and the time_low, time_mid, and time_hi fields are set to the least, middle and most significant bits (respectively) of a 60-bit timestamp measured in 100-nanosecond
 * units since midnight, October 15, 1582 UTC.
 */
public final class ServiceID implements Serializable {

    private static final long serialVersionUID = -7803375959559762239L;

    /**
     * The most significant 64 bits.
     *
     * @serial
     */
    private final long mostSig;
    /**
     * The least significant 64 bits.
     *
     * @serial
     */
    private final long leastSig;

    /**
     * Simple constructor.
     *
     * @param mostSig
     *            the most significant 64 bits
     * @param leastSig
     *            the lease significant 64 bits
     */
    public ServiceID(long mostSig, long leastSig) {
        this.mostSig = mostSig;
        this.leastSig = leastSig;
    }

    /**
     * Reads in 16 bytes in standard network byte order.
     *
     * @param in
     *            the input stream to read 16 bytes from
     */
    public ServiceID(DataInput in) throws IOException {
        this.mostSig = in.readLong();
        this.leastSig = in.readLong();
    }

    /**
     * Returns the most significant 64 bits of the service ID.
     */
    public long getMostSignificantBits() {
        return mostSig;
    }

    /**
     * Returns the least significant 64 bits of the service ID.
     */
    public long getLeastSignificantBits() {
        return leastSig;
    }

    /**
     * Writes out 16 bytes in standard network byte order.
     *
     * @param out
     *            the output stream to write 16 bytes to
     */
    public void writeBytes(DataOutput out) throws IOException {
        out.writeLong(mostSig);
        out.writeLong(leastSig);
    }

    public int hashCode() {
        return (int) ((mostSig >> 32) ^ mostSig ^ (leastSig >> 32) ^ leastSig);
    }

    /**
     * Service IDs are equal if they represent the same 128-bit value.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ServiceID))
            return false;
        ServiceID sid = (ServiceID) obj;
        return (mostSig == sid.mostSig && leastSig == sid.leastSig);
    }

    /**
     * Returns a 36-character string of six fields separated by hyphens, with each field represented in lowercase hexadecimal with the same number of digits as in the field. The order of fields is:
     * time_low, time_mid, version and time_hi treated as a single field, variant and clock_seq treated as a single field, and node.
     */
    public String toString() {
        return (digits(mostSig >> 32, 8) + "-" + digits(mostSig >> 16, 4) + "-" + digits(mostSig, 4) + "-" + digits(leastSig >> 48, 4) + "-" + digits(leastSig, 12));
    }

    /**
     * Returns val represented by the specified number of hex digits.
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
}
