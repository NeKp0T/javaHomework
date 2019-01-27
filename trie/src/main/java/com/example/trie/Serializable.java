package com.example.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Provides interface for serializing and deserializing objects.
 * Data acquired by <code>serialize</code> is to be deserializable by <code>deserialize</code>.
 * Methods are expected to throw NullPointerException then given null stream
 */
public interface Serializable {
    /**
     * @param out the stream to feed serialized data
     * @throws IOException if any problems with the stream occur
     * @throws NullPointerException if specified stream is null
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * @param in the stream to read data from
     * @throws IOException if any problems with the stream occur
     * @throws NullPointerException if specified stream is null
     */
    void deserialize(InputStream in) throws IOException;
}