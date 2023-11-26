package ru.ermolaev.services.data.actuator.classifier;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;
import ru.ermolaev.services.data.actuator.model.AbstractMigrationModel;

public class MigrationDataClassifier<T extends AbstractMigrationModel> implements Classifier<T, ItemWriter<T>> {

    private final ItemWriter<T> createWriter;

    private final ItemWriter<T> updateWriter;

    public MigrationDataClassifier(ItemWriter<T> createWriter, ItemWriter<T> updateWriter) {
        this.createWriter = createWriter;
        this.updateWriter = updateWriter;
    }

    @Override
    public ItemWriter<T> classify(T migrationModel) {
        switch (migrationModel.getWriteStrategy()) {
            case CREATE:
                return createWriter;
            case UPDATE:
                return updateWriter;
            default:
                throw new UnsupportedOperationException();
        }
    }

}