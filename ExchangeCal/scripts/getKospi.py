import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.gridspec import  GridSpec

sns.set_style('whitegrid')

grid = GridSpec(4, 1, wspace = 0.3, hspace = 0.5)
fig = plt.figure(0)

ax1 = fig.add_subplot(grid[0:2, 0:1])
ax2 = fig.add_subplot(grid[2:3, 0:1], sharex = ax1)
ax3 = fig.add_subplot(grid[3:4, 0:1], sharex = ax1)

ax1.plot(kospi200.Close)
ax1.set_ylabel('KOSPI 200')
ax2.plot(kospi200.PER, c = 'r', lw = 1)
ax2.set_ylabel('PER')
ax3.plot(kospi200.PBR, c = 'g', lw = 1)
ax3.set_ylabel('PBR')

plt.show()
