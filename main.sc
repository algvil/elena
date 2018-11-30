
theme: /
    @IntentGroup
        {
          "boundsTo" : "",
          "actions" : [
            {
              "type" : "buttons",
              "buttons" : [ ]
            }
          ],
          "global" : false,
          "intents" : [
            {
              "phrases" : [
                {
                  "type" : "pattern",
                  "value" : "*$dates $city_code::from $city_code::dest"
                },
                {
                  "type" : "pattern",
                  "value" : "*$city_code::from * $city_code::dest * $dates *"
                },
                {
                  "type" : "pattern",
                  "value" : "*$dates * $city_code::from * $city_code::dest *"
                }
              ],
              "then" : "/newNode_6"
            },
            {
              "phrases" : [
                {
                  "type" : "example",
                  "value" : "не могу войти *$pass"
                },
                {
                  "type" : "example",
                  "value" : "сменить *$pass"
                }
              ],
              "then" : "/newNode_4"
            },
            {
              "phrases" : [
                {
                  "type" : "pattern",
                  "value" : "*$city_code::from * $city_code::dest *"
                },
                {
                  "type" : "pattern",
                  "value" : "*$city_code::from в $city_code::dest *"
                },
                {
                  "type" : "pattern",
                  "value" : "*из $city_code::from в $city_code::dest *"
                }
              ],
              "then" : "/newNode_10"
            },
            {
              "phrases" : [
                {
                  "type" : "pattern",
                  "value" : "$hello"
                }
              ],
              "then" : "/newNode_12"
            }
          ]
        }
    state: newNode_0
        state: 1
            q: *$dates $city_code::from $city_code::dest
            q: *$city_code::from * $city_code::dest * $dates *
            q: *$dates * $city_code::from * $city_code::dest *

            go!: /newNode_6

        state: 2
            e: не могу войти *$pass
            e: сменить *$pass

            go!: /newNode_4

        state: 3
            q: *$city_code::from * $city_code::dest *
            q: *$city_code::from в $city_code::dest *
            q: *из $city_code::from в $city_code::dest *

            go!: /newNode_10

        state: 4
            q: $hello

            go!: /newNode_12
        init:
            $jsapi.bind({
                type: "postProcess",
                path: "/newNode_0",
                name: "newNode_0 buttons",
                handler: function($context) {
                }
            });

    state: newNode_2
        random:
            a:  Ошибка поиска {{$session.httpStatus}} , переменные from = {{$session.from}} , =
                , dates={{$session.dates}}
                урл http://partners.ozon.travel/search_v1_0/flight/?Flight={{$session.from}}&Date1={{$session.dates}}&Dlts=1&Children=0&Infants=0&ServiceClass=ECONOMY
                dest {{$session.dest}} || tts = "", ttsEnabled = false
        go!: /newNode_9
    @Transition
        {
          "boundsTo" : "/newNode_2",
          "then" : "/newNode_0"
        }
    state: newNode_9
        go!: /newNode_0

    state: newNode_4
        random:
            a: Вы можете восстановить пароль пройдя по этой ссылке https://www.ozon.ru/context/forgotpassword/ || tts = "", ttsEnabled = false
        go!: /newNode_7
    @Transition
        {
          "boundsTo" : "/newNode_4",
          "then" : "/newNode_0"
        }
    state: newNode_7
        go!: /newNode_0

    state: newNode_5
        random:
            a:  Маршрут {{$session.c_from}} {{$session.f_airport}} - {{$session.c_dest}} {{$session.d_airport}}
                Минимальная цена перелёта {{$session.prices}} руб.
                вылет {{$session.dates}} {{$session.fromTime}}
                Результаты поиска здесь - https://www.ozon.travel/flight/search/{{$session.from}}{{$session.dest}}/d{{$session.dates}}/?Dlts=1 || tts = "", ttsEnabled = false
        go!: /newNode_8
    @Transition
        {
          "boundsTo" : "/newNode_5",
          "then" : "/newNode_0"
        }
    state: newNode_8
        go!: /newNode_0
    @HttpRequest
        {
          "boundsTo" : "",
          "actions" : [
            {
              "type" : "buttons",
              "buttons" : [ ]
            }
          ],
          "url" : "http://algvil.ru/test_json.php?FROM=${from}&DEST=${dest}&DATES=${dates}",
          "method" : "GET",
          "dataType" : "json",
          "body" : "",
          "okState" : "/newNode_5",
          "errorState" : "/newNode_2",
          "timeout" : 0,
          "headers" : [ ],
          "vars" : [
            {
              "name" : "f_airport",
              "value" : "$httpResponse.data[0].segments[0].flights[0].flightLegs[0].from.code"
            },
            {
              "name" : "c_from",
              "value" : "$httpResponse.data[0].segments[0].flights[0].flightLegs[0].from.city"
            },
            {
              "name" : "prices",
              "value" : "$httpResponse.data[0].prices[0]"
            },
            {
              "name" : "c_dest",
              "value" : "$httpResponse.data[0].segments[0].flights[0].flightLegs[0].to.city"
            },
            {
              "name" : "d_airport",
              "value" : "$httpResponse.data[0].segments[0].flights[0].flightLegs[0].to.code"
            },
            {
              "name" : "fromTime",
              "value" : "$httpResponse.data[0].segments[0].flights[0].flightLegs[0].fromTime"
            }
          ]
        }
    state: newNode_6
        script:
            var headers = {
            };
            var result = $http.query("http://algvil.ru/test_json.php?FROM=${from}&DEST=${dest}&DATES=${dates}", {
                method: "GET",
                headers: headers,
                query: $session,
                dataType: "json",
                timeout: 0 || 10000
            });
            var $httpResponse = result.data;
            $session.httpStatus = result.status;
            $session.httpResponse = $httpResponse;
            if (result.isOk && result.status >= 200 && result.status < 300) {
                $session["f_airport"] = $httpResponse.data[0].segments[0].flights[0].flightLegs[0].from.code;
                $session["c_from"] = $httpResponse.data[0].segments[0].flights[0].flightLegs[0].from.city;
                $session["prices"] = $httpResponse.data[0].prices[0];
                $session["c_dest"] = $httpResponse.data[0].segments[0].flights[0].flightLegs[0].to.city;
                $session["d_airport"] = $httpResponse.data[0].segments[0].flights[0].flightLegs[0].to.code;
                $session["fromTime"] = $httpResponse.data[0].segments[0].flights[0].flightLegs[0].fromTime;
                $reactions.transition("/newNode_5");
            } else {
                $reactions.transition("/newNode_2");
            }
        init:
            $jsapi.bind({
                type: "postProcess",
                path: "/newNode_6",
                name: "newNode_6 buttons",
                handler: function($context) {
                }
            });

    state: newNode_10
        random:
            a: На какую дату вас интересуют билеты? || tts = "", ttsEnabled = false
        go!: /newNode_11
    @IntentGroup
        {
          "boundsTo" : "/newNode_10",
          "actions" : [
            {
              "type" : "buttons",
              "buttons" : [ ]
            }
          ],
          "global" : false,
          "fallback" : "/newNode_10",
          "intents" : [
            {
              "phrases" : [
                {
                  "type" : "pattern",
                  "value" : "$dates"
                }
              ],
              "then" : "/newNode_6"
            }
          ]
        }
    state: newNode_11
        state: 1
            q: $dates

            go!: /newNode_6

        state: Fallback
            q: *
            go!: /newNode_10
        init:
            $jsapi.bind({
                type: "postProcess",
                path: "/newNode_11",
                name: "newNode_11 buttons",
                handler: function($context) {
                }
            });

    state: newNode_12
        random:
            a: Приветствую || tts = "", ttsEnabled = false
        go!: /newNode_13
    @Transition
        {
          "boundsTo" : "/newNode_12",
          "then" : "/newNode_0"
        }
    state: newNode_13
        go!: /newNode_0
