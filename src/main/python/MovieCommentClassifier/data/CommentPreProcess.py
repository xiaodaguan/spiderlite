# coding:utf-8

import jieba
# import nltk
import numpy as np
import tensorflow as tf
from nltk import WordNetLemmatizer
from pymongo import MongoClient


# nltk.download('wordnet')

def get_data_from_mongodb(limit=None):
    conn = MongoClient('localhost', 27017)
    db = conn['spider_db']
    db.authenticate('spider', '123456')
    coll = db.collection_names()
    # print('\n'.join(coll))

    commColl = db['doubanComment_list']
    # document = commColl.find_one()
    # print(json.dumps(document, ensure_ascii=False))
    docs = commColl.find()
    if limit:
        limit_docs_pos = []
        limit_docs_neg = []
        for doc in docs:
            if int(doc['score']) <= 2 and len(limit_docs_neg) < limit:
                limit_docs_neg.append(doc)
            elif int(doc['score']) >= 4 and len(limit_docs_pos) < limit:
                limit_docs_pos.append(doc)
            if len(limit_docs_pos) >= limit and len(limit_docs_neg) >= limit:
                break
        limit_docs_pos.extend(limit_docs_neg)
        return limit_docs_pos
    else:
        return docs


def get_stopwords():
    stopwords = []
    with open('data/stopwords.txt', mode='r', encoding='utf-8') as f:
        for line in f.readlines():
            stopwords.append(line.strip())
    print(stopwords)
    return stopwords


def get_sents(docs):
    praises = []
    poors = []
    for doc in docs:
        if int(doc['score']) <= 2:
            poors.append(doc)
        elif int(doc['score']) >= 4:
            praises.append(doc)

    # 输出好评差评
    with open('neg.txt', mode='w', encoding='utf-8') as f:
        for doc in poors:
            # print(doc['﻿comment'.replace('\ufeff', '')])
            f.write(doc['﻿comment'.replace('\ufeff', '')])
            f.write('\n')
            f.flush()
    with open('pos.txt', mode='w', encoding='utf-8') as f:
        for doc in praises:
            f.write(doc['﻿comment'.replace('\ufeff', '')])
            f.write('\n')
            f.flush()
    print('好评:{0}, 差评:{1}'.format(len(praises), len(poors)))

    # 全部句子
    all_sents = []
    pos_sents = []
    neg_sents = []
    with open('data/pos.txt', mode='r', encoding='utf-8') as fPos:
        for line in fPos.readlines():
            all_sents.append(line)
            pos_sents.append(line)
    with open('data/neg.txt', mode='r', encoding='utf-8') as fNeg:
        for line in fNeg.readlines():
            all_sents.append(line)
            neg_sents.append(line)
    print('all sents: ', len(all_sents))
    return all_sents, pos_sents, neg_sents


# 分词、统计词频、创建词表
def get_lexicon(all_sents, stopwords):
    global sent, lexicon
    word_count = {}
    word_set = set()
    for idx, sent in enumerate(all_sents):
        if idx % 1000 == 0:
            print('reading sent: ', idx)
        for word in jieba.cut(sent):
            if word in stopwords:
                continue
            word_set.add(word)
            # if word not in word_count:
            #     word_count[word] = 1
            # else:
            #     word_count[word] += 1
    # print("word count:", word_count)
    lexicon = {}
    for i, word in enumerate(word_set):
        lexicon[word] = i
    print("lexicon: ", lexicon)
    return lexicon


# word to vector
def word_to_vector(_lex, review):
    '''

    :param _lex: 词表
    :param review:
    :return:
    '''
    words = jieba.cut(review)
    lemmatizer = WordNetLemmatizer()
    words = [lemmatizer.lemmatize(word) for word in words]
    features = np.zeros(len(_lex))
    for word in words:
        if word in _lex:
            features[_lex[word]] = 1
    return features


def get_dataset(pos_sents, neg_sents):
    ds = []
    for idx, sent in enumerate(pos_sents):
        if idx % 1000 == 0:
            print('creating dataset: ', idx)
        one_sample = [word_to_vector(lexicon, sent), [1, 0]]
        ds.append(one_sample)

    for idx, sent in enumerate(neg_sents):
        if idx % 1000 == 0:
            print('creating dataset: ', idx)
        one_sample = [word_to_vector(lexicon, sent), [0, 1]]
        ds.append(one_sample)

    print("samples: ", ds)
    return ds


def neural_network(_lex, data):
    n_input_layer = len(_lex)
    n_layer_1 = 2000
    n_layer_2 = 2000
    n_output_layer = 2

    layer_1_w_b = {
        'w_': tf.Variable(tf.random_normal([n_input_layer, n_layer_1])),
        'b_': tf.Variable(tf.random_normal([n_layer_1]))
    }

    layer_2_w_b = {
        'w_': tf.Variable(tf.random_normal([n_layer_1, n_layer_2])),
        'b_': tf.Variable(tf.random_normal([n_layer_2]))
    }

    layer_output_w_b = {
        'w_': tf.Variable(tf.random_normal([n_layer_2, n_output_layer])),
        'b_': tf.Variable(tf.random_normal([n_output_layer]))
    }

    layer_1 = tf.add(tf.matmul(data, layer_1_w_b['w_']), layer_1_w_b['b_'])
    layer_1 = tf.nn.relu(layer_1)
    layer_2 = tf.add(tf.matmul(layer_1, layer_2_w_b['w_']), layer_2_w_b['b_'])
    layer_2 = tf.nn.relu(layer_2)
    layer_output = tf.add(tf.matmul(layer_2, layer_output_w_b['w_']), layer_output_w_b['b_'])
    return layer_output


def train_neural_network(_lex, ds, epochs, batch_size):
    _dataset = np.array(ds)

    x = tf.placeholder('float', [None, len(_dataset[0][0])])
    y = tf.placeholder('float')

    predict = neural_network(_lex, x)
    cost_func = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=predict, labels=y))
    optimizer = tf.train.AdamOptimizer().minimize(cost_func)

    with tf.Session() as session:
        saver = tf.train.Saver()
        session.run(tf.global_variables_initializer())
        train_x = _dataset[:, 0]
        train_y = _dataset[:, 1]
        for epoch in range(epochs):
            i = 0
            epochs_loss = 0
            while i < len(train_x):
                if i % 128 == 0 or i % 100 == 0:
                    print('batch: ', i)
                run_metadata = tf.RunMetadata()
                start = i
                end = i + batch_size
                batch_x = train_x[start:end]
                batch_y = train_y[start:end]
                _, c = session.run([optimizer, cost_func], feed_dict={x: list(batch_x), y: list(batch_y)})
                epochs_loss += c
                i += batch_size

            print(epoch, ' : ', epochs_loss)

            text_x = _dataset[:, 0]
            text_y = _dataset[:, 1]
            correct = tf.equal(tf.argmax(predict, 1), tf.argmax(y, 1))
            accuracy = tf.reduce_mean(tf.cast(correct, 'float'))

            print('准确率: ', accuracy.eval({x: list(text_x), y: list(text_y)}))

        saver.save(session, './data/model.ckpt')

        writer = tf.summary.FileWriter("./data")
        writer.add_graph(session.graph)


# 使用模型预测, [0] good, [1] bad
def prediction(_lex, text):
    x = tf.placeholder('float')
    predict = neural_network(_lex, x)
    with tf.Session() as session:
        session.run(tf.global_variables_initializer())
        saver = tf.train.import_meta_graph('model.ckpt.meta')
        all_vars = tf.trainable_variables()
        # for v in all_vars:
        #     print(v.name)
        saver.restore(session, tf.train.latest_checkpoint('./'))
        print('model restored.')
        all_vars = tf.trainable_variables()
        # for v in all_vars:
        #     print(v.name, v.eval(session))
        features = word_to_vector(_lex, text)
        res = session.run(tf.argmax(predict.eval(feed_dict={x: [features]}), 1))
        return res[0] == 0


# 停用词表
stopwords = get_stopwords()

# 加载评论
docs = get_data_from_mongodb(limit=10000)

# 句子集
all_sents, pos_sents, neg_sents = get_sents(docs)

# 创建词表
lexicon = get_lexicon(all_sents, stopwords)

# 创建数据集
ds = get_dataset(pos_sents, neg_sents)

# 训练
print('training...', np.shape(ds))
train_neural_network(lexicon, ds, 10, 50)

# 预测
print('predicting...')
print(prediction(lexicon, "好电影"))  # True
print(prediction(lexicon, "垃圾电影"))  # False
print(prediction(lexicon, "无聊的片子"))  # False
print(prediction(lexicon, "好评！！"))  # True
print(prediction(lexicon, '简直烂到家了。'))  # False
print(prediction(lexicon, '全程无尿点'))  # True
print(prediction(lexicon, '垃圾不解释'))  # False
