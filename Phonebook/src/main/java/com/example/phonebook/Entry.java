package com.example.phonebook;

import org.jetbrains.annotations.NotNull;

// tuple of name, phone and id.
final class Entry {
    @NotNull
    public final String name;
    public final String phone;
    public final int id;

    public Entry(int id, String nameString, String phoneNumberString) {
        this.id = id;
        name = nameString;
        phone = phoneNumberString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            Entry that = (Entry) obj;
            return name.equals(that.name) && phone.equals(that.phone) && id == that.id;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + phone;
    }
}