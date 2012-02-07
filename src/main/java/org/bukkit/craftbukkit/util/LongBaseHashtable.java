package org.bukkit.craftbukkit.util;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.bukkit.craftbukkit.util.Java15Compat.Arrays_copyOf;

public class LongBaseHashtable extends LongHash {

    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    EntryBase[][][] values = new EntryBase[256][][];
    EntryBase cache = null;

    public void put(int msw, int lsw, EntryBase entry) {
        put(entry);
    }

    public EntryBase getEntry(int msw, int lsw) {
        return getEntry(toLong(msw, lsw));
    }

    public void put(EntryBase entry) {
        lock.writeLock().lock();
        try {
            int mainIdx = (int) (entry.key & 255);
            EntryBase[][] outer = this.values[mainIdx];
            if (outer == null) this.values[mainIdx] = outer = new EntryBase[256][];
    
            int outerIdx = (int) ((entry.key >> 32) & 255);
            EntryBase[] inner = outer[outerIdx];
    
            if (inner == null) {
                outer[outerIdx] = inner = new EntryBase[5];
                inner[0] = this.cache = entry;
            } else {
                int i;
                for (i = 0; i < inner.length; i++) {
                    if (inner[i] == null || inner[i].key == entry.key) {
                        inner[i] = this.cache = entry;
                        return;
                    }
                }
    
                outer[outerIdx] = inner = Arrays_copyOf(inner, i + i);
                inner[i] = entry;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public EntryBase getEntry(long key) {
        return containsKey(key) ? cache : null;
    }

    public boolean containsKey(long key) {
        lock.readLock().lock();
        try {
            if (this.cache != null && cache.key == key) return true;
    
            int outerIdx = (int) ((key >> 32) & 255);
            EntryBase[][] outer = this.values[(int) (key & 255)];
            if (outer == null) return false;
    
            EntryBase[] inner = outer[outerIdx];
            if (inner == null) return false;
    
            for (int i = 0; i < inner.length; i++) {
                EntryBase e = inner[i];
                if (e == null) {
                    return false;
                } else if (e.key == key) {
                    this.cache = e;
                    return true;
                }
            }
            return false;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public synchronized void remove(long key) {
        lock.writeLock().lock();
        try {
            EntryBase[][] outer = this.values[(int) (key & 255)];
            if (outer == null) return;
    
            EntryBase[] inner = outer[(int) ((key >> 32) & 255)];
            if (inner == null) return;
    
            for (int i = 0; i < inner.length; i++) {
                if (inner[i] == null) continue;
    
                if (inner[i].key == key) {
                    for (i++; i < inner.length; i++) {
                        if (inner[i] == null) break;
                        inner[i - 1] = inner[i];
                    }
    
                    inner[i-1] = null;
                    this.cache = null;
                    return;
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public ArrayList<EntryBase> entries() {
        lock.readLock().lock();
        try {
            ArrayList<EntryBase> ret = new ArrayList<EntryBase>();
    
            for (EntryBase[][] outer : this.values) {
                if (outer == null) continue;
    
                for (EntryBase[] inner : outer) {
                    if (inner == null) continue;
    
                    for (EntryBase entry : inner) {
                        if (entry == null) break;
    
                        ret.add(entry);
                    }
                }
            }
            return ret;
        }
        finally {
            lock.readLock().unlock();
        }
    }
}
