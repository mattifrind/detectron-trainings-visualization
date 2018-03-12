# Detectron trainings visualization
With this software you can visualize your Detectron training process. The software reads the accuracy, the loss, the loss_cls and the loss_bbox. Optionally you can create regressions for the different series in different orders. You can also import two training stats if you trained in two diferent steps.

When you train you network just save the standard out into a file and paste it into the software directory. For example use this command: 
```
python2 tools/train_net.py --cfg path/to/config/file.yaml OUTPUT_DIR path/to/output/directory | tee /home/ubuntu/output/60000train.txt
```

You can configure the script with the settings at the top of the script.

When you run the software you should get a chart like this:
![example chart](https://github.com/mattifrind/detectron-trainings-visualization/output.png)