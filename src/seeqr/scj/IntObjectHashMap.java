package seeqr.scj;

// Copyright (c) 2013 Gratian Lup. All rights reserved.
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following
// disclaimer in the documentation and/or other materials provided
// with the distribution.
//
// * The name "CompactCollections" must not be used to endorse or promote
// products derived from this software without prior written permission.
//
// * Products derived from this software may not be called "CompactCollections" nor
// may "CompactCollections" appear in their names without prior written
// permission of the author.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
import java.util.*;

public class IntObjectHashMap<T> extends AbstractMap<Integer, T> {
    public static class MapEntry<T> implements Map.Entry<Integer, T> {
        private int key;
        private T value;

        public MapEntry(int key, T value) {
            this.key = key;
            this.value = value;
        }

        public Integer getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public T setValue(T newValue) {
            T oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object object) {
            if (!(object instanceof MapEntry)) {
                return false;
            }

            MapEntry other = (MapEntry)object;
            return key == other.getKey() &&
                    value.equals(other.getValue());
        }

        public int hashCode() {
            return key ^ (value != null ? value.hashCode() : 0);
        }
    }

    // The size of the hash table. Each item in the table "points" to
    // (is the index of) the first value having that hash code.
    private static final int DEFAULT_TABLE_SIZE = 8;

    // The size of the table containing the buckets of the hash table.
    // Each element contains the key and "points" to the next entry in the bucket.
    // Note that all buckets are stored interleaved in the same array.
    private static final int DEFAULT_BUCKET_TABLE_SIZE = 32;

    // The size of the table containing the actual values.
    // The values are stored in the order they were added.
    private static final int DEFAULT_DATA_TABLE_SIZE = 32;

    // The load factor of the hash table (how much space is used).
    // Increasing the load factor reduces the memory consumption,
    // but increases search time, in worst case reaching linear search.
    // A value of 4 seems to be the best in most cases.
    private static final int LOAD_FACTOR = 4;

    private int[] table;    // Start index of buckets.
    private Object[] data;  // Value for corresponding Bucket.
    private long[] buckets; // <Key, Next Table Index> pairs.
    private int count;      // The total number of values in the map.

    public IntObjectHashMap() {
        resetToDefault();
    }

    public IntObjectHashMap(Set<Entry<Integer, T>> values) {
        this();
        for(Entry<Integer, T> pair : values) {
            put(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public T get(Object key) {
        if(!(key instanceof Integer)) {
            throw new IllegalArgumentException("Key is not an Integer!");
        }

        int temp = (Integer)key;
        return get(temp);
    }

    @Override
    public T put(Integer key, T value) {
        int tempKey = (Integer)key;
        return put(tempKey, value);
    }

    @Override
    public void clear() {
        resetToDefault();
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<Integer, T>> entrySet() {
        return new AbstractSet<Entry<Integer, T>>() {
            @Override
            public Iterator<Entry<Integer, T>> iterator() {
                // Not the most efficient implementation, building
                // the pair list could be avoided by using a custom iterator.
                List<Entry<Integer, T>> pairs = new ArrayList<Entry<Integer, T>>(count);

                for(int i = 0; i < count; i++) {
                    pairs.add(new MapEntry<T>(extractKey(buckets[i]), (T)data[i]));
                }

                return pairs.iterator();
            }

            @Override
            public int size() {
                return count;
            }
        };
    }

    private void resetToDefault() {
        table = new int[DEFAULT_TABLE_SIZE];
        Arrays.fill(table, -1);

        buckets = new long[DEFAULT_BUCKET_TABLE_SIZE];
        data = new Object[DEFAULT_DATA_TABLE_SIZE];
        count = 0;
    }

    private long packValues(int key, int next) {
        // Placing the key and next values together increases the search speed:
        // when the key is not the searched one the index of the next location
        // to check is already loaded into the lowest CPU cache.
        return ((long)next << 32) | ((long)key & 0xFFFFFFFFL);
    }

    private int extractKey(long value) {
        return (int)value;
    }

    private int extractNext(long value) {
        return (int)(value >>> 32);
    }

    private long replaceKey(int newA, long value) {
        return (value & 0xFFFFFFFF00000000L) | ((long)newA & 0xFFFFFFFFL);
    }

    private long replaceNext(int newB, long value) {
        return (value & 0xFFFFFFFFL) | ((long)newB << 32);
    }

    private int getNextTableSize(int currentSize) {
        // Don't let the hash table grow beyond 32 bit indices.
        long nextSize = currentSize * 2;
        return nextSize < Integer.MAX_VALUE ? (int)nextSize : currentSize;
    }

    int findChainEnd(int index) {
        long bucket = buckets[index];
        int next = extractNext(bucket);

        while(next != -1) {
            index = next;
            bucket = buckets[index];
            next = extractNext(bucket);
        }

        return index;
    }

    private void resizeTables(int requiredSize) {
        if(requiredSize >= data.length) {
            int bucketSize = buckets.length;
            long[] newBuckets = new long[buckets.length * 2];
            System.arraycopy(buckets, 0, newBuckets, 0, buckets.length);
            buckets = newBuckets;

            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }

        if(count / LOAD_FACTOR >= table.length) {
            // Resize the table and rehash the start values.
            int newTableSize = getNextTableSize(table.length);

            if(newTableSize <= table.length) {
                return; // Table shouldn't grow further.
            }

            // Create a new table and rehash the bucket start keys
            // into the new table. On conflict the buckets are chained.
            table = new int[newTableSize];
            Arrays.fill(table, -1);

            for(int i = 0; i < count; i++) {
                long bucket = buckets[i];
                int key = extractKey(bucket);
                int keyHash = computeHash(key);

                int previousBucketIndex = table[keyHash];
                table[keyHash] = i;

                if(previousBucketIndex != -1) {
                    // The bucket that was associated with the hash code
                    // must be added at the end of the current bucket.
                    buckets[i] = replaceNext(previousBucketIndex, bucket);
                }
                else {
                    // This is a single-element bucket chain (it is possible
                    // to be included in another chain at a later step).
                    buckets[i] = replaceNext(-1, bucket);
                }
            }
        }
    }

    private int computeHash(int key) {
        // The table length should always be a power of two.
        assert((table.length & (table.length - 1)) == 0);
        return key & (table.length - 1);
    }

    private int findBucketIndex(int key, boolean returnLast) {
        int keyHash = computeHash(key);
        int bucketIndex = table[keyHash];
        int lastBucketIndex = bucketIndex;

        while(bucketIndex != -1) {
            long bucket = buckets[bucketIndex];
            int bucketKey = extractKey(bucket);

            if(bucketKey == key) {
                return bucketIndex;
            }
            else {
                lastBucketIndex = bucketIndex;
                bucketIndex = extractNext(bucket);
            }
        }

        return returnLast ? lastBucketIndex : -1;
    }

    @SuppressWarnings("unchecked")
    public T get(int key) {
        int dataIndex = findBucketIndex(key, false /* returnLast */);

        if(dataIndex != -1) {
            return (T)data[dataIndex];
        }
        else return null;
    }

    private int appendData(int key, T value) {
        buckets[count] = packValues(key, -1 /* end of bucket chain */);
        data[count] = value;
        return count++;
    }

    @SuppressWarnings("unchecked")
    public T put(int key, T value) {
        // Check if the key is already in the table.
        // If it is, the new value is used.
        resizeTables(count);
        int bucketIndex = findBucketIndex(key, true /* returnLast */);

        if(bucketIndex != -1) {
            long bucket = buckets[bucketIndex];
            int bucketKey = extractKey(bucket);

            if(bucketKey == key) {
                // The same key has been found.
                T oldValue = (T)data[bucketIndex];
                data[bucketIndex] = value;
                return oldValue;
            }
            else {
                // A new entry must be added at the end of the bucket.
                int dataIndex = appendData(key, value);
                buckets[bucketIndex] = replaceNext(dataIndex, bucket);
            }
        }
        else {
            // No bucket is associated with the hash code yet.
            int dataIndex = appendData(key, value);
            int keyHash = computeHash(key);
            table[keyHash] = dataIndex;
        }

        return null;
    }

    public boolean containsKey(int key) {
        return findBucketIndex(key, false /* returnLast */) != -1;
    }
}