# 概要

これはエムスリー株式会社が開催している [Kotlinジェネリクス勉強会](https://connpass.com/event/78536/) のメモである。  
このメモの内容はほとんど [初学者向け「Kotlinでジェネリクスを学ぼう」](https://speakerdeck.com/ntaro/chu-xue-zhe-xiang-ke-kotlindezienerikusuwoxue-bou) から書き取った物なので、こちらを読んだほうが良いかもしれない。

(このリポジトリにあるKotlinコードは特に何も書いてないです)

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
func setDefault(box: MutableBox<in Int>) {
  box.value = 0
}

val box: MutableBox<Number> = MutableBox(NaN) // NanはDouble型だが、Numberにキャストされる
setDefault(box) // Intの引数にNumber型のBoxが渡されている
println(box.value) // 0 (Number型)
```

# 不変・共変・反変のまとめ

型Aが型Bのサブタイプである時、

- 不変はサブタイピングが成り立たない
- 共変は型Aと型Bのサブタイピングが成り立つ (出力のみ出来る)
- 反変は型Aと型Bの逆のサブタイピングが成り立つ (入力のみ出来る)

# 型プロジェクション(2回目)

- 型の射影
- キーワード out や in を使う
- 「使用場所変位指定」と言うこともある
- immutableな変数は変更出来ないのだから `out` パラメータは冗長なのでは？

これに対して「宣言場所変位指定」を使う。

# 宣言場所変位指定

- 型パラメータを宣言する場所に `out` や `in` を使う
- 危険な操作を公開するとコンパイルエラー

```kt
// ここで指定する
class Box<out T>(val value: T)
```

# ジェネリック制約

- 型引数として指定出来る型に制約を設ける
- 制約とは、具体的には上限境界

```kt
// このクラスの型パラメータはNumber以下の型しか指定出来ない
class NumberBox<out T: Number>(val value: T) {
  func toInt(): NumberBox<Int> = 
    NumberBox(value.toInt())
}
```

# 複数の上限境界

- whererキーワードを使う

```kt
// TODO: あとで書く
```

# 型消去

```kt
val box: Box<String> = Box("Hello"9
val value: String = box.value
```

↓ コンパイル

```java
Box box = new Box("Hello");
String value = (String) box.getValue(); // キャストが行われる
```

- 後方互換のため、Javaではジェネリクスの情報を削除している
- その為、コンパイル後は型情報が失われる (型消去)
- 型安全は人間の物なのでコンパイル後は型情報は必要無い (大抵の場合)
- しかし、型チェックを行う場合は型情報が確定しないためエラーになる

```kt
// NG
val object: Any = Box<Int>(123)
if (object is Box<Int>) { ... }

// OK
val object: Any = Box<Int>(123)
if (object is Box<*>) { ... } // * をスタープロジェクションと呼ぶ
```

# スタープロジェクション

- 型が決まっているが、不明な時 or 興味が無い時に使用する
- <out Any?> かつ <in Nothing>的に振る舞う
- Any?はあらゆる型のスーパータイプとして振る舞い、Nothingはあらゆる型のサブタイプとなる

```kt
val list: MutableList<*> = MutableListOf<Int>()
val value: Any? = list.get(0)
list.add(123) // コンパイルエラー, Nothingの為
```

# 具象型(reified type)パラメータ付き関数

- reified キーワードを使うと、コンパイル時も型情報を保つことが出来る

```kt
inline fun <reified T> Any.isA(): Boolean = this is T
5.isA<Number>()
```

# まとめ

任意の型に対して汎用的に安全なコード部品化を実現可能 (ジェネリクス)。
ジェネリクス型を安全かつ柔軟に扱う為に変位と呼ばれる性質を利用する(不変, 共変, 反変)



















