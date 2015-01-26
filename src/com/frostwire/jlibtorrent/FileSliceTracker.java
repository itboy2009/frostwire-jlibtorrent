package com.frostwire.jlibtorrent;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author gubatron
 * @author aldenml
 */
public final class FileSliceTracker {

    private final int fileIndex;
    private final TreeMap<Long, Pair<FileSlice, Boolean>> slices;

    public FileSliceTracker(int fileIndex) {
        this.fileIndex = fileIndex;
        this.slices = new TreeMap<Long, Pair<FileSlice, Boolean>>();
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public long getSequentialDownloaded() {
        Iterator<Pair<FileSlice, Boolean>> it = slices.values().iterator();

        long downloaded = 0;
        boolean done = false;

        while (!done && it.hasNext()) {
            Pair<FileSlice, Boolean> p = it.next();

            if (Boolean.TRUE.equals(p.second)) { // complete
                downloaded = downloaded + p.first.getSize();
            } else {
                done = true;
            }
        }

        return downloaded;
    }

    public void addSlice(FileSlice slice) throws IllegalArgumentException {
        if (slice.getFileIndex() != fileIndex) {
            throw new IllegalArgumentException("Invalid file index");
        }

        slices.put(slice.getOffset(), new Pair<FileSlice, Boolean>(slice, Boolean.FALSE));
    }

    public int getNumSlices() {
        return slices.size();
    }

    public boolean isComplete(long offset) throws IllegalArgumentException {
        Pair<FileSlice, Boolean> p = slices.get(offset);
        if (p == null) {
            throw new IllegalArgumentException("offset is not contained in the internal structure");
        }
        return Boolean.TRUE.equals(p.second);
    }

    public void setComplete(long offset, boolean complete) throws IllegalArgumentException {
        Pair<FileSlice, Boolean> p = slices.get(offset);
        if (p == null) {
            throw new IllegalArgumentException("offset is not contained in the internal structure");
        }
        slices.put(offset, new Pair<FileSlice, Boolean>(p.first, Boolean.valueOf(complete)));
    }
}