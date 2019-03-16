


// После загрузки DOM-дерева (страницы)
$(function() {
    var numPages = 0; // Сколько всего страниц в БД с данной видимостью
    var numImages = 0; // Сколько всего фото в базе данных с данной видимостью
    var onPage = 0; // Число фото на странице
    var nextPage = 1; // Следующая страница для загрузки
    var visible = 1; // Текущая директория
    var imgCount = 0; // Сколько фото отображено

    $('#actions').hide();
    $('#actHid').hide();
    $('#actVis').show();
    upd();
    updImgCount();

    // Выбор папки
    $('#dirVis').on('click',function (e) {
        e.preventDefault();
        visible = 1;
        $('#actHid').hide();
        $('#actVis').show();
        $('#btnAdd').show();
        clean();
        upd();
        updImgCount();
    })
    $('#dirHid').on('click',function (e) {
        e.preventDefault();
        visible = 0;
        $('#actHid').show();
        $('#actVis').hide();
        $('#btnAdd').hide();
        clean();
        upd();
        updImgCount();
    })

    // Загрузить одну фотографию
    function loadImg(num, vis){
        $.ajax({
            type: "GET",
            url: "/getPhoto?num=" + num + "&vis=" + vis,
            cache: false,
            success: function (html) {
                html = JSON.parse(html);
                imgCount += Number(html.count);
                $("#content").append(html.content);
            }
        })
    }

    // Кнопка "Восстановить"
    $('#btnUnHid').on('click',function(e){
        e.preventDefault();
        var url = $('#actions').find('img').attr('src');
        $.ajax({
            type: "POST",
            url: "/updPhoto",
            data:{'url':url,'visible':'1', 'type':'vis'},
            cache: false,
            success: function(r) {
                $('#actions').hide();
                var url = $('#actions').find('img').attr('src');
                var to = $('a.thumbnail>img[src="'+url+'"]').parent();
                to.parent().remove();
                imgCount-=1;
                loadImg(imgCount + 1, visible);
                updImgCount();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + " " + errorThrown);
            }
        });
    });

    // Кнопка "Удалить"
    $('#btnDel').on('click',function (e) {
        e.preventDefault();
        var url = $('#actions').find('img').attr('src');
        $.ajax({
            type: "POST",
            url: "/updPhoto",
            data:{'url':url, 'type':'del'},
            cache: false,
            success: function(r) {
                $('#actions').hide();
                var url = $('#actions').find('img').attr('src');
                var to = $('a.thumbnail>img[src="'+url+'"]').parent();
                to.parent().remove();
                imgCount -= 1;
                loadImg(imgCount+1, visible);
                updImgCount();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + " " + errorThrown);
            }
        });
    });

    // Кнопка "Скрыть"
    $('#btnHide').on('click',function(e){
        e.preventDefault();
        var url = $('#actions').find('img').attr('src');
        $.ajax({
            type: "POST",
            url: "/updPhoto",
            data:{'url':url,'visible':'0', 'type':'vis'},
            cache: false,
            success: function(r) {
                $('#actions').hide();
                var url = $('#actions').find('img').attr('src');
                var to = $('a.thumbnail>img[src="'+url+'"]').parent();
                to.parent().remove();
                imgCount-=1;
                loadImg(imgCount+1, visible);
                updImgCount();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + " " + errorThrown);
            }
        });
    });

    // Кнопка "Сохранить"
    $('#btnSave').on('click',function (e) {
        e.preventDefault();
        var url = $('#actions').find('img').attr('src');
        var to = $('a.thumbnail>img[src="'+url+'"]').parent();
        var sdescr = $('#actions .sdescr').val();
        var ldescr = $('#actions .sdescr').val();
        var data = {
            'url':url,
            'sdescr':sdescr,
            'ldescr':ldescr,
            'type':'descr'
        };
        $.ajax({
            type: "POST",
            url: "/updPhoto",
            data:data,
            cache: false,
            success: function(r) {
                copy($('#actions'),to);
            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + " " + errorThrown);
            }
        });
    });

    // Кнопка "добавить" в верхней части экрана
    $('#btnAdd').on("click", function(e) {
        e.preventDefault();
        //открыть модальное окно
        $('#inputFile').val();
        $("#image-modal").modal('show');
    });

    // кнопка подтверждения загрузки
    $('#confirmLoading').on("click",function(e){
        e.preventDefault();
        var data = new FormData;
        data.append('file', $('#inputFile').prop('files')[0]);
        data.append('sdescr',$('#loaddescrs').val());
        data.append('ldescr',$('#loaddescrl').val());
        $.ajax({
            type: "POST",
            url: "/addImage",
            data:data,
            cache: false,
            processData: false,
            contentType: false,
            success: function(r) {
                clean();
                upd();
                $('#image-modal').modal('hide');
                updImgCount();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert(textStatus);
            }
        });
    });

    //при нажатии на ссылку, содержащую Thumbnail
    $(document).on("click", "a.thumbnail", function(e) {
        e.preventDefault();
        copy($(this), $('#actions'));
        $('#actions').show();
    });

    //при нажатии на картинку справа
    $(document).on("click", ".zoomable", function(e) {
        e.preventDefault();
        copyF($(this).parent(), $('.gzoom'));
        $('.gzoom').modal('show');
    });

    //при нажатию на изображение внутри модального окна
    $('.gzoom .modal-body img').on('click', function() {
        $(".gzoom").modal('hide');
    });

    // Очистить контейнер с картинками
    function clean(){
        $('#actions').hide();
        $('#content').html('');
        imgCount = 0;
        nextPage = 1;
    };

    // Запросить страницу для контейнера с картинками
    function upd(){
        getCount().then(function() {
            if (nextPage <= numPages) {
                $.ajax({
                    type: "GET",
                    url: "/getPhoto?page=" + nextPage + "&vis=" + visible,
                    cache: false,
                    success: function (html) {
                        html = JSON.parse(html);
                        imgCount += Number(html.count);
                        $("#content").append(html.content);
                    }
                });
                nextPage += 1;
            }
        });
    };

    // Узнать кол-во страниц и изображений
    function getCount(){
        return $.ajax({
            type: "POST",
            url: "/getPhoto?vis="+visible,
            cache: false,
            success: function(data){
                data = JSON.parse(data);
                numPages = Number(data.pages);
                numImages = Number(data.images);
                onPage = Number(data.onpage);
            },
            error: function () {
                alert("can't get count of pages");
            }
        });
    }
    // Обновить надпись с числом картинок
    function updImgCount() {
        getCount().then(function (value) {
            $('#imgCount').html(numImages);
        })
    }
    // Скролл контента
    $('#content').scroll(function(){
        if (this.scrollTop == this.scrollHeight - this.clientHeight && nextPage<=numPages){
            $('#loading').show();
            upd();
            $('#loading').hide();
        }
    });

});
