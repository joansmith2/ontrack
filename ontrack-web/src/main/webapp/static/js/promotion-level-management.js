var PromotionLevelManagement = function () {

    function unlink (validationStampItem) {
        // The validation stamp
        var validationStamp = validationStampItem.attr('data-validationStamp');
        // The previous promotion level
        var promotionLevel = validationStampItem.attr('data-promotionLevel');
        if (promotionLevel && promotionLevel != '') {
            var project = $('#project').val();
            var branch = $('#branch').val();
            // Starts indicating the loading
            var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
            Application.loading(promotionLevelLoadingIndicator, true);
            // Ajax to perform the unlink
            Application.ajaxGet(
                'ui/manage/promotion_level/{0}/{1}/{2}/unlink'.format(
                    project.html(),
                    branch.html(),
                    validationStamp.html()
                ),
                function (data) {
                    Application.loading(promotionLevelLoadingIndicator, false);
                    if (data.success) {
                        postDnD(validationStampItem, $('#freeValidationStamps'), false);
                    }
                },
                function (message) {
                    Application.loading(promotionLevelLoadingIndicator, false);
                    Application.displayError(message);
                }
            );
        }
    }

    function postDnD (validationStampItem, target, promotionLevel) {
        // Prepares for alignment
        validationStampItem.removeAttr('style');
        // Appends the validation stamp item to the target
        validationStampItem.appendTo(target);
        // Set-ups the promotion level to this stamp
        if (promotionLevel) {
            validationStampItem.attr('data-promotionLevel', promotionLevel);
        } else {
            validationStampItem.removeAttr('data-promotionLevel');
        }
        // Initializes all promotion drop zones
        initDropZones();
    }

    function link (validationStampItem, promotionLevelItem) {
        var promotionLevel = promotionLevelItem.attr('data-promotionLevel');
        var validationStamp = validationStampItem.attr('data-validationStamp');
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Starts indicating the loading
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        Application.loading(promotionLevelLoadingIndicator, true);
        // Ajax to perform the link
        Application.ajaxGet(
            'ui/manage/promotion_level/{0}/{1}/{2}/link/{3}'.format(
                project.html(),
                branch.html(),
                validationStamp.html(),
                promotionLevel.html()
            ),
            function (data) {
                Application.loading(promotionLevelLoadingIndicator, false);
                if (data.success) {
                    // Clears the DnD style for the validation stamp item
                    postDnD(validationStampItem, promotionLevelItem, promotionLevel);
                }
            },
            function (message) {
                Application.loading(promotionLevelLoadingIndicator, false);
                Application.displayError(message);
            }
        );
    }

    function initDropZones () {
        $('.promotionLevelStamps').each(function (index, promotionLevelItem) {
            var dropZone = $('#dropzone-label-' + $(promotionLevelItem).attr('data-promotionLevel'));
            if ($(promotionLevelItem).find('span.validationStamp').length > 0) {
                dropZone.hide();
            } else {
                dropZone.show();
            }
        });
    }

    function init () {
        // All validation stamps are draggable
        $('span.validationStamp').draggable({
            revert: "invalid",
            cursor: "move"
        });
        // Initializes all promotion drop zones
        initDropZones();
        // Droppable zones for the promotion levels
        $('.promotionLevelStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                var promotionLevelItem = $(this);
                var validationStampItem = ui.draggable;
                link(validationStampItem, promotionLevelItem);
                return false;
            }
        });
        // Droppable zone for the free validation stamps
        $('#freeValidationStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                var validationStampItem = ui.draggable;
                unlink(validationStampItem);
                return false;
            }
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init)