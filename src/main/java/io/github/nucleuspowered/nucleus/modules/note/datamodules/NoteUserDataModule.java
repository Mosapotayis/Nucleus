/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.datamodules;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.dataservices.modular.DataKey;
import io.github.nucleuspowered.nucleus.dataservices.modular.DataModule;
import io.github.nucleuspowered.nucleus.dataservices.modular.ModularUserService;
import io.github.nucleuspowered.nucleus.iapi.data.NoteData;

import java.util.List;

public class NoteUserDataModule extends DataModule<ModularUserService> {

    @DataKey("notes")
    private List<NoteData> notes = Lists.newArrayList();

    public List<NoteData> getNotes() {
        return ImmutableList.copyOf(notes);
    }

    public void addNote(NoteData note) {
        if (notes == null) {
            notes = Lists.newArrayList();
        }

        notes.add(note);
    }

    public boolean removeNote(NoteData note) {
        return notes.removeIf(x -> x.getNoter().equals(note.getNoter()) && x.getNote().equals(note.getNote()));
    }

    public boolean clearNotes() {
        if (!notes.isEmpty()) {
            notes.clear();
            return true;
        } else {
            return false;
        }
    }
}
