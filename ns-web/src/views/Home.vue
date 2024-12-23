<template>
  <div class="home">
    <div class="poems-container">
      <div v-for="poem in currentPagePoems" :key="poem.id" class="poem-card">
        <comment-item
          :comment="poem.comment"
          :userId="userId"
          :poemTitle="poem.title"
          @update-comment="updateComment"
        >
          <template #poem-content>
            <div class="poem-content">
              <h2 class="poem-title">{{ poem.title }}</h2>
              <p class="poem-author">{{ poem.author }}</p>
              <p class="poem-text">{{ poem.content }}</p>
              <div class="poem-meta">
                <span class="visit-count">访问量: {{ poem.visitCount }}</span>
                <span class="like-count">点赞数: {{ poem.likeCount }}</span>
              </div>
            </div>
          </template>
        </comment-item>
      </div>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="poemsPerPage"
        :total="totalPoems"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
import CommentItem from '../components/CommentItem.vue';

export default {
  name: 'Home',
  components: {
    CommentItem
  },
  data() {
    return {
      poems: [
        {
          id: 1,
          title: '春晓',
          author: '孟浩然',
          content: '春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。',
          visitCount: 1234,
          likeCount: 88,
          comment: {
            id: 1,
            content: '这首诗将春日清晨的美好意境描绘得淋漓尽致。',
            likeCount: 15,
            likedUserIds: [],
            replies: [
              {
                id: 101,
                username: '文学爱好者',
                content: '尤其是最后一句"花落知多少"，既点明时节，又暗含惋惜之情。',
                time: '2024-01-15 10:30',
                likes: 8,
                isLiked: false,
                replyTo: '诗词鉴赏家'
              },
              {
                id: 102,
                username: '古诗词研究',
                content: '这首诗的语言看似平淡，却韵味悠长。',
                time: '2024-01-15 11:00',
                likes: 12,
                isLiked: false,
                replies: [
                  {
                    id: 1021,
                    username: '唐诗爱好者',
                    content: '确实，简单的文字传达出丰富的意境。',
                    time: '2024-01-15 11:15',
                    likes: 6,
                    isLiked: false,
                    replyTo: '古诗词研究'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 2,
          title: '静夜思',
          author: '李白',
          content: '床前明月光，疑是地上霜。举头望明月，低头思故乡。',
          visitCount: 2345,
          likeCount: 120,
          comment: {
            id: 2,
            content: '这首诗写尽了游子思乡之情，千古传诵。',
            likeCount: 18,
            likedUserIds: [],
            replies: [
              {
                id: 201,
                username: '诗词品鉴',
                content: '月光、思乡、孤独，意象的运用恰到好处。',
                time: '2024-01-15 12:30',
                likes: 15,
                isLiked: false,
                replies: [
                  {
                    id: 2011,
                    username: '月下独酌',
                    content: '是啊，这种思乡之情谁都感同身受。',
                    time: '2024-01-15 12:45',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词品鉴'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 3,
          title: '登鹳雀楼',
          author: '王之涣',
          content: '白日依山尽，黄河入海流。欲穷千里目，更上一层楼。',
          visitCount: 1876,
          likeCount: 95,
          comment: {
            id: 3,
            content: '气势恢宏，展现出诗人开阔的胸襟和远大的志向。',
            likeCount: 20,
            likedUserIds: [],
            replies: [
              {
                id: 301,
                username: '诗海漫游',
                content: '登高望远，诗人的视角层层推进，令人叹服。',
                time: '2024-01-15 13:30',
                likes: 12,
                isLiked: false,
                replies: [
                  {
                    id: 3011,
                    username: '文学探索',
                    content: '最后一句"更上一层楼"展现了积极进取的精神。',
                    time: '2024-01-15 13:45',
                    likes: 8,
                    isLiked: false,
                    replyTo: '诗海漫游'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 4,
          title: '望庐山瀑布',
          author: '李白',
          content: '日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。',
          visitCount: 1567,
          likeCount: 78,
          comment: {
            id: 4,
            content: '诗人将庐山瀑布的磅礴气势描绘得淋漓尽致。',
            likeCount: 16,
            likedUserIds: [],
            replies: [
              {
                id: 401,
                username: '山水诗评',
                content: '李白的想象力真是惊人，"银河落九天"这个比喻绝妙。',
                time: '2024-01-15 14:30',
                likes: 10,
                isLiked: false,
                replies: [
                  {
                    id: 4011,
                    username: '诗词品读',
                    content: '确实，将瀑布比作银河，既体现了其规模，又突出了其壮丽。',
                    time: '2024-01-15 14:45',
                    likes: 6,
                    isLiked: false,
                    replyTo: '山水诗评'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 5,
          title: '送杜少府之任蜀州',
          author: '王勃',
          content: '城阙辅三秦，风烟望五津。与君离别意，同是宦游人。',
          visitCount: 890,
          likeCount: 45,
          comment: {
            id: 5,
            content: '一首情真意切的送别诗，字字含情。',
            likeCount: 14,
            likedUserIds: [],
            replies: [
              {
                id: 501,
                username: '诗词赏析',
                content: '诗人以自己同为宦游人的身份，表达对友人的深切共鸣。',
                time: '2024-01-15 15:00',
                likes: 9,
                isLiked: false,
                replies: [
                  {
                    id: 5011,
                    username: '古韵悠扬',
                    content: '最后一句点睛之笔，将离别之情推向高潮。',
                    time: '2024-01-15 15:15',
                    likes: 5,
                    isLiked: false,
                    replyTo: '诗词赏析'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 6,
          title: '相思',
          author: '王维',
          content: '红豆生南国，春来发几枝。愿君多采撷，此物最相思。',
          visitCount: 2156,
          likeCount: 134,
          comment: {
            id: 6,
            content: '以红豆寄托相思之情，含蓄委婉，意境优美。',
            likeCount: 19,
            likedUserIds: [],
            replies: [
              {
                id: 601,
                username: '诗词雅韵',
                content: '王维善用物象，红豆既是实物，又是情思的寄托。',
                time: '2024-01-15 15:30',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 6011,
                    username: '古诗今读',
                    content: '是啊，"此物最相思"一句更是点明主题。',
                    time: '2024-01-15 15:45',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词雅韵'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 7,
          title: '登高',
          author: '杜甫',
          content: '风急天高猿啸哀，渚清沙白鸟飞回。无边落木萧萧下，不尽长江滚滚来。',
          visitCount: 1678,
          likeCount: 89,
          comment: {
            id: 7,
            content: '诗人以望远，将秋日萧瑟之景描绘得淋漓尽致。',
            likeCount: 17,
            likedUserIds: [],
            replies: [
              {
                id: 701,
                username: '诗词鉴赏',
                content: '杜甫的这首诗将秋天的肃杀之气表现得很到位。',
                time: '2024-01-15 16:00',
                likes: 12,
                isLiked: false,
                replies: [
                  {
                    id: 7011,
                    username: '唐诗品读',
                    content: '尤其是"无边落木萧萧下"这句，让人感受到深秋的萧瑟。',
                    time: '2024-01-15 16:15',
                    likes: 8,
                    isLiked: false,
                    replyTo: '诗词鉴赏'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 8,
          title: '望天门山',
          author: '李白',
          content: '天门中断楚江开，碧水东流至此回。两岸青山相对出，孤帆一片日边来。',
          visitCount: 1432,
          likeCount: 76,
          comment: {
            id: 8,
            content: '诗人以开阔的视角描绘天门山的雄伟景色。',
            likeCount: 15,
            likedUserIds: [],
            replies: [
              {
                id: 801,
                username: '山水诗话',
                content: '李白笔下的天门山气势磅礴，画面感极强。',
                time: '2024-01-15 16:30',
                likes: 10,
                isLiked: false,
                replies: [
                  {
                    id: 8011,
                    username: '诗词品鉴',
                    content: '末句点睛之笔，一叶孤帆为整幅画面增添了生机。',
                    time: '2024-01-15 16:45',
                    likes: 6,
                    isLiked: false,
                    replyTo: '山水诗话'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 9,
          title: '早发白帝城',
          author: '李白',
          content: '朝辞白帝彩云间，千里江陵一日还。两岸猿声啼不住，轻舟已过万重山。',
          visitCount: 1890,
          likeCount: 98,
          comment: {
            id: 9,
            content: '诗人以飘逸的笔触描绘了一幅行舟图，意境开阔。',
            likeCount: 18,
            likedUserIds: [],
            replies: [
              {
                id: 901,
                username: '诗词解析',
                content: '这首诗展现了诗人乘舟顺流而下的畅快之感。',
                time: '2024-01-15 17:00',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 9011,
                    username: '古诗鉴赏',
                    content: '"轻舟已过万重山"一句尤其精彩，体现了速度感。',
                    time: '2024-01-15 17:15',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词解析'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 10,
          title: '黄鹤楼送孟浩然之广陵',
          author: '李白',
          content: '故人西辞黄鹤楼，烟花三月下扬州。孤帆远影碧空尽，唯见长江天际流。',
          visitCount: 2034,
          likeCount: 112,
          comment: {
            id: 10,
            content: '一首意境深远的送别诗，将离别之情表达得淋漓尽致。',
            likeCount: 20,
            likedUserIds: [],
            replies: [
              {
                id: 1001,
                username: '诗词品读',
                content: '最后两句写得极好，将送别之情与江天辽阔融为一体。',
                time: '2024-01-15 17:30',
                likes: 13,
                isLiked: false,
                replies: [
                  {
                    id: 10011,
                    username: '古诗鉴赏',
                    content: '是啊，"孤帆远影"与"长江天际"的意象运用恰到好处。',
                    time: '2024-01-15 17:45',
                    likes: 8,
                    isLiked: false,
                    replyTo: '诗词品读'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 11,
          title: '送友人',
          author: '李白',
          content: '青山横北郭，白水绕东城。此地一为别，孤蓬万里征。',
          visitCount: 1245,
          likeCount: 67,
          comment: {
            id: 11,
            content: '短短四句诗，道尽离别之情，格局开阔，意境深远。',
            likeCount: 16,
            likedUserIds: [],
            replies: [
              {
                id: 1101,
                username: '诗词解读',
                content: '前两句写景，后两句抒情，结构严谨，意境优美。',
                time: '2024-01-15 18:00',
                likes: 10,
                isLiked: false,
                replies: [
                  {
                    id: 11011,
                    username: '唐诗鉴赏',
                    content: '"孤蓬万里征"一句尤其精彩，展现了游子漂泊之感。',
                    time: '2024-01-15 18:15',
                    likes: 6,
                    isLiked: false,
                    replyTo: '诗词解读'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 12,
          title: '春夜喜雨',
          author: '杜甫',
          content: '好雨知时节，当春乃发生。随风潜入夜，润物细无声。',
          visitCount: 1678,
          likeCount: 89,
          comment: {
            id: 12,
            content: '诗人以细腻的笔触描绘春雨，体现了对生机的欣喜。',
            likeCount: 18,
            likedUserIds: [],
            replies: [
              {
                id: 1201,
                username: '诗词赏析',
                content: '杜甫将春雨的特点描写得如此传神，令人叹服。',
                time: '2024-01-15 18:30',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 12011,
                    username: '古韵今读',
                    content: '"润物细无声"这句写得极妙，既写出了春雨的特点，又暗含春意。',
                    time: '2024-01-15 18:45',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词赏析'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 13,
          title: '绝句',
          author: '杜甫',
          content: '两个黄鹂鸣翠柳，一行白鹭上青天。窗含西岭千秋雪，门泊东吴万里船。',
          visitCount: 1890,
          likeCount: 94,
          comment: {
            id: 13,
            content: '诗人以精妙的笔触勾勒出一幅生动的春日图景。',
            likeCount: 17,
            likedUserIds: [],
            replies: [
              {
                id: 1301,
                username: '诗词品鉴',
                content: '这首诗将动静结合，远近相映，构图精妙。',
                time: '2024-01-15 19:00',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 13011,
                    username: '古诗今读',
                    content: '尤其是"窗含西岭千秋雪"这句，将远景收入眼底。',
                    time: '2024-01-15 19:15',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词品鉴'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 14,
          title: '清明',
          author: '杜牧',
          content: '清明时节雨纷纷，路上行人欲断魂。借问酒家何处有，牧童遥指杏花村。',
          visitCount: 2134,
          likeCount: 108,
          comment: {
            id: 14,
            content: '诗人以细腻的笔触描绘清明时节的景象，意境深远。',
            likeCount: 19,
            likedUserIds: [],
            replies: [
              {
                id: 1401,
                username: '诗词鉴赏',
                content: '这首诗将清明时节的氛围描写得如此传神。',
                time: '2024-01-15 19:30',
                likes: 12,
                isLiked: false,
                replies: [
                  {
                    id: 14011,
                    username: '唐诗品读',
                    content: '最后一句"牧童遥指杏花村"点题，令人心生向往。',
                    time: '2024-01-15 19:45',
                    likes: 8,
                    isLiked: false,
                    replyTo: '诗词鉴赏'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 15,
          title: '山行',
          author: '杜牧',
          content: '远上寒山石径斜，白云生处有人家。停车坐爱枫林晚，霜叶红于二月花。',
          visitCount: 1567,
          likeCount: 78,
          comment: {
            id: 15,
            content: '一首描写秋景的佳作，将秋天的美景描绘得栩栩如生。',
            likeCount: 16,
            likedUserIds: [],
            replies: [
              {
                id: 1501,
                username: '诗词解析',
                content: '诗人以独特的视角捕捉秋天的美，尤其是最后一句极妙。',
                time: '2024-01-15 20:00',
                likes: 10,
                isLiked: false,
                replies: [
                  {
                    id: 15011,
                    username: '古韵悠扬',
                    content: '"霜叶红于二月花"这个比喻新颖独特。',
                    time: '2024-01-15 20:15',
                    likes: 6,
                    isLiked: false,
                    replyTo: '诗词解析'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 16,
          title: '题都城南庄',
          author: '崔护',
          content: '去年今日此门中，人面桃花相映红。人面不知何处去，桃花依旧笑春风。',
          visitCount: 1789,
          likeCount: 92,
          comment: {
            id: 16,
            content: '诗人以桃花衬托人事变迁，令人感慨万千。',
            likeCount: 18,
            likedUserIds: [],
            replies: [
              {
                id: 1601,
                username: '诗词品读',
                content: '诗中的"人面桃花"意象运用得极为巧妙。',
                time: '2024-01-15 20:30',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 16011,
                    username: '古诗鉴赏',
                    content: '最后一句"桃花依旧笑春风"更是点睛之笔。',
                    time: '2024-01-15 20:45',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词品读'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 17,
          title: '江雪',
          author: '柳宗元',
          content: '千山鸟飞绝，万径人踪灭。孤舟蓑笠翁，独钓寒江雪。',
          visitCount: 1567,
          likeCount: 82,
          comment: {
            id: 17,
            content: '短短四句，勾勒出一幅孤寂而静谧的雪景图。',
            likeCount: 17,
            likedUserIds: [],
            replies: [
              {
                id: 1701,
                username: '诗词赏析',
                content: '诗人以极其简练的笔触，描绘出广阔的意境。',
                time: '2024-01-15 21:00',
                likes: 10,
                isLiked: false,
                replies: [
                  {
                    id: 17011,
                    username: '唐诗品读',
                    content: '尤其是"千山"、"万径"的用词，突出了雪景的辽阔。',
                    time: '2024-01-15 21:15',
                    likes: 6,
                    isLiked: false,
                    replyTo: '诗词赏析'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 18,
          title: '登乐游原',
          author: '李商隐',
          content: '向晚意不适，驱车登古原。夕阳无限好，只是近黄昏。',
          visitCount: 1432,
          likeCount: 76,
          comment: {
            id: 18,
            content: '诗人以夕阳景色暗喻人生，意境深远。',
            likeCount: 16,
            likedUserIds: [],
            replies: [
              {
                id: 1801,
                username: '诗词解读',
                content: '最后两句"夕阳无限好，只是近黄昏"写得极为精妙。',
                time: '2024-01-15 21:30',
                likes: 9,
                isLiked: false,
                replies: [
                  {
                    id: 18011,
                    username: '古韵今读',
                    content: '这种对美好事物即将消逝的惋惜之情，令人感同身受。',
                    time: '2024-01-15 21:45',
                    likes: 5,
                    isLiked: false,
                    replyTo: '诗词解读'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 19,
          title: '枫桥夜泊',
          author: '张继',
          content: '月落乌啼霜满天，江枫渔火对愁眠。姑苏城外寒山寺，夜半钟声到客船。',
          visitCount: 2145,
          likeCount: 115,
          comment: {
            id: 19,
            content: '诗人以细腻的笔触描绘夜景，意境深远而凄美。',
            likeCount: 19,
            likedUserIds: [],
            replies: [
              {
                id: 1901,
                username: '诗词鉴赏',
                content: '这首诗将听觉、视觉意象完美结合。',
                time: '2024-01-15 22:00',
                likes: 12,
                isLiked: false,
                replies: [
                  {
                    id: 19011,
                    username: '古诗品读',
                    content: '尤其是最后一句"夜半钟声到客船"，将寂寞之感推向高潮。',
                    time: '2024-01-15 22:15',
                    likes: 8,
                    isLiked: false,
                    replyTo: '诗词鉴赏'
                  }
                ]
              }
            ]
          }
        },
        {
          id: 20,
          title: '望岳',
          author: '杜甫',
          content: '岱宗夫如何，齐鲁青未了。造化钟神秀，阴阳割昏晓。荡胸生层云，决眦入归鸟。',
          visitCount: 1678,
          likeCount: 88,
          comment: {
            id: 20,
            content: '诗人以雄浑的笔触描绘泰山，气势磅礴。',
            likeCount: 17,
            likedUserIds: [],
            replies: [
              {
                id: 2001,
                username: '诗词解析',
                content: '这首诗将泰山的雄伟气势描写得淋漓尽致。',
                time: '2024-01-15 22:30',
                likes: 11,
                isLiked: false,
                replies: [
                  {
                    id: 20011,
                    username: '唐诗鉴赏',
                    content: '"荡胸生层云，决眦入归鸟"两句尤其精彩。',
                    time: '2024-01-15 22:45',
                    likes: 7,
                    isLiked: false,
                    replyTo: '诗词解析'
                  }
                ]
              }
            ]
          }
        }
      ],
      userId: 'user1',
      currentPage: 1,
      poemsPerPage: 10,
      totalPoems: 20
    };
  },
  computed: {
    currentPagePoems() {
      const startIndex = (this.currentPage - 1) * this.poemsPerPage;
      return this.poems.slice(startIndex, startIndex + this.poemsPerPage);
    }
  },
  methods: {
    handlePageChange(newPage) {
      this.currentPage = newPage;
      window.scrollTo(0, 0); // 回到顶部
    },
    updateComment(updatedComment) {
      const poem = this.poems.find(p => p.comment.id === updatedComment.id);
      if (poem) {
        poem.comment = updatedComment;
      }
    }
  }
};
</script>

<style scoped>
/* 添加全局样式，确保页面占满全高 */
:deep(html), :deep(body) {
  height: 100%;
  margin: 0;
  padding: 0;
}

.home {
  min-height: 100vh;
  width: 100%;
  position: relative;
  background: none;
}

/* 背景图容器 */
.home::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('/VCG211324156765.jpg') no-repeat center center;
  background-size: cover;
  z-index: 0;
}

/* 内容遮罩层 */
.home::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6);
  z-index: 1;
}

.poems-container {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.poem-card {
  background-color: rgba(255, 255, 255, 0);
  padding: 30px 20px;
  border: none;
  position: relative;
  backdrop-filter: none;
  border-radius: 0;
  transition: none;
}

.poem-content {
  text-align: center;
}

.poem-title {
  font-size: 24px;
  color: #000;
  margin-bottom: 15px;
  font-weight: normal;
  letter-spacing: 3px;
}

.poem-author {
  color: #333;
  font-size: 16px;
  margin-bottom: 25px;
  letter-spacing: 2px;
}

.poem-text {
  font-size: 18px;
  line-height: 2;
  color: #000;
  margin-bottom: 25px;
  white-space: pre-wrap;
  letter-spacing: 2px;
  text-shadow: none;
}

.poem-meta {
  display: flex;
  justify-content: center;
  color: #666;
  font-size: 14px;
  gap: 30px;
  margin-bottom: 0;
  opacity: 0.8;
}

.pagination {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
  background: none;
  border: none;
}

/* 自定义分页样式 */
:deep(.el-pagination) {
  --el-pagination-button-bg-color: transparent;
  --el-pagination-hover-color: #666;
  --el-pagination-font-size: 14px;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  background: transparent;
  border: 1px solid #ddd;
}

:deep(.el-pagination .el-pager li) {
  background: transparent;
  border: 1px solid #ddd;
}

:deep(.el-pagination .el-pager li.active) {
  color: #333;
  font-weight: bold;
  border-color: #999;
}

.visit-count, .like-count {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
