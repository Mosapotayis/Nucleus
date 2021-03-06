/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.datamodules;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.dataservices.modular.DataKey;
import io.github.nucleuspowered.nucleus.dataservices.modular.DataModule;
import io.github.nucleuspowered.nucleus.dataservices.modular.ModularUserService;
import io.github.nucleuspowered.nucleus.iapi.data.mail.MailData;

import java.util.List;

public class MailUserDataModule extends DataModule<ModularUserService> {

    @DataKey("mail")
    private List<MailData> mailDataList = Lists.newArrayList();

    public List<MailData> getMail() {
        return ImmutableList.copyOf(mailDataList);
    }

    public void addMail(MailData mailData) {
        if (mailDataList == null) {
            mailDataList = Lists.newArrayList();
        }

        mailDataList.add(mailData);
    }

    public boolean removeMail(MailData mailData) {
        return mailDataList.removeIf(x -> x.getDate().equals(mailData.getDate()) &&
                x.getMessage().equals(mailData.getMessage()) && x.getUuid().equals(mailData.getUuid()));
    }

    public boolean clearMail() {
        if (!mailDataList.isEmpty()) {
            mailDataList.clear();
            return true;
        } else {
            return false;
        }
    }
}
