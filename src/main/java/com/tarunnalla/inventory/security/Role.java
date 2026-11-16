package com.tarunnalla.inventory.security;

public enum Role {
    /** Can view items and record stock movements. */
    USER,
    /** Can also create, edit and delete items. */
    ADMIN
}
