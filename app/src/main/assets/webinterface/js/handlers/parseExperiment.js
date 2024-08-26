import Experiment from '../classes/experiment.js';
import Block from '../classes/block.js';
import Possibility from '../classes/possibility.js';

function parseExperimentData(data) {
  const experiment = new Experiment(data.experiment.name);

  // Create and add blocks
  const blockMap = {};
  const order = data.sequence.order.split(',');

  for (const id of order) {
    const blockData = data.sequence[id];
    if (blockData) {
      const block = new Block(id, blockData);
      if (blockData.possibilities) {
        for (const pId in blockData.possibilities) {
          const pData = blockData.possibilities[pId];
          const possibility = new Possibility(pId, pData.value);
          block.addPossibility(possibility);
        }
      }
      experiment.addBlock(block);
      blockMap[id] = block;
    }
  }

  return experiment;
}

export default parseExperimentData;
