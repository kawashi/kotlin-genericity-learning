# 概要

これはエムスリー株式会社が開催しているKotlinジェネリクス勉強会の内容である。  
[初学者向け「Kotlinでジェネリクスを学ぼう」](https://connpass.com/event/78536/)

# ジェネリクスとは？

あらゆる型に対応させたいが、キャストはしたくない。(キャストは危険)  
↓
そこで、ジェネリクスを使う。

# ジェネリッククラス

- 型パラメータが宣言されているクラス
- 型パラメータとは仮の型、大文字一文字が慣習(必ずしも一文字である必要は無い)

```kt
class Box<T>(val value: T))
```

利用する際は下記のようにする。  
(ほとんどの場合は型推論してくれるので指定する必要は無い)

```kt
val box1: Box<String> = Box<String>("Hello")
val box2: Box<Int> = Box<Int>(3)

// キャストが不要
val string: String = box1.value
val int: Int = box2.value
```

# 型パラメータ?型引数?

- 仮の宣言: 型パラメータ (type parameter)
- 実際の指定: 型引数 (type argument)

# 変位とは

- 英語では variance, 変位や分散などと呼ばれる
- サブタイピングの関係を記述する
- 不変, 共変, 反変がある
- クラス != 型
- KotlinではString型とString?型が出来るので、1クラスに1つの型とは限らない
- ジェネリッククラスの場合は1クラスで無限の型がある

# 不変(invariant)

- 不変だとimmutableと混ざるので、非変と呼ばれることもある
- サブタイプの関係が成り立たない
- デフォルト

```kt
val int: Int = 123
val number: Number = int // 出来る

val box1: Box<Int> = box(123)
val box2: Box<Number> = box1 // 不変なのでコンパイルエラー
```

不変だと扱いづらい場合がある。  
例えば、FloatをNumberを受け取るメソッドに渡して、toIntしたい時等である。  
ここで、共変を使う。

# 共変(convariant)

- 型パラメータと同じサブタイピング関係が成り立つ
- 型プロダクションにおいて `out` キーワドを用いる

```kt
val box1: Box<Int> = Box(123)
val box2: Box<out Number> = box1 // OK
```

## 型プロジェクションとは？

- 型の射影である(projection)、圏論っぽい言葉が出てきた...
- 射影とは、集合の選択 (集合のある側面にだけ注目する)
- 型プロダクションも型のある側面だけに注目する
- つまり、ある側面を隠している


```kt
class MutableBox<T>(var value: T)

val box1: MutableBox<Int> = MutableBox(123)
val box2: MutableBox<out Number> = box1
box2.value = 0.5 // これは出来そうだが、コンパイルエラーが起こる
```

- 型パラメータはNumberだが、実装はInt型なのでDouble型を代入すると危険
- これはコンパイラで禁止すべき操作
- Javaの配列では可能だが、実行時に例外をthrowする (なのでJavaは型安全ではない)

上記が何故コンパイルエラーになるのかというと、setterが削除されているため。  
  -> 型プロジェクションによってある型の側面を隠した (setterを隠した)

このため、安全に共変を扱う事ができる。

# 反変(contravariant)

- 型パラメータと逆のサブタイピング関係が成り立つ
- 反変では `in` キーワードを用いる
- 反変の場合は共変とは逆で、getterが削除されている

```kt
func setDefault(box: MutableBox<in Number>) {
  box.value = 0
}

val box: MutableBox<Number> = MutableBox(NaN) // NanはDouble型である
setDefault(box)
```

# 不変・共変・反変のまとめ

型Aが型Bのサブタイプである時、

- 不変はサブタイピングが成り立たない
- 共変は型Aと型Bのサブタイピングが成り立つ (出力のみ出来る)
- 反変は型Aと型Bの逆のサブタイピングが成り立つ (入力のみ出来る)

# 変位の指定

// TODO: これから





















