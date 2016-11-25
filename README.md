## clipboard
https://developer.android.com/guide/topics/text/copy-paste.html
支持三种格式
    Text    String
    URI     一个URI
    Intent  一个Intent
使用 ,主要用到 ClipboardManager  https://developer.android.com/reference/android/content/ClipboardManager.html
ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
然后创建一个 ClipData 对象， ClipData.Item 包含  text, URI, or Intent data;
## 复制数据至 ClipData:
    For text:
    ClipData clip = ClipData.newPlainText(label, text);
    For a URI:
    ClipData clip = ClipData.newUri(resolver, label, URI);
    For an Intent:
    ClipData clip = ClipData.newIntent(label, intent);// intent = new Intent(this, xxx.class)
将ClipData赋值给clipboard
    clipboard.setPrimaryClip(clip);

## 从 clipboard 得到数据
    ClipboardManager中有很多判断与操作方法：
    getPrimaryClip()	            返回剪贴板上的当前Copy内容
    getPrimaryClipDescription()	    返回剪贴板上的当前Copy的说明
    hasPrimaryClip()	            如果当前剪贴板上存在Copy返回True
    setPrimaryClip(ClipData clip)	设置剪贴板上的当前Copy
    setText(CharSequence text)	    设置文本到当前Copy
    getText()	                    获取剪贴板复制的文本
Text:
第一步,得到全局的ClipboardManager
    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
第二步,开启或关闭"paste"选项,判断clipboard是否有ClipData
    // Gets the ID of the "paste" menu item
    MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

    // If the clipboard doesn't contain data, disable the paste menu item.
    // If it does contain data, decide if you can handle the data.
    if (!(clipboard.hasPrimaryClip())) {

        mPasteItem.setEnabled(false);

    } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
        // This disables the paste menu item, since the clipboard has data but it is not plain text
        mPasteItem.setEnabled(false);
    } else {
        // This enables the paste menu item, since the clipboard contains plain text.
        mPasteItem.setEnabled(true);
    }
第三步,复制数据
    // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
    // text. Assumes that this application can only handle one item at a time.
     ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
    // Gets the clipboard as text.
    pasteData = item.getText();
    // If the string contains data, then the paste operation is done
    if (pasteData != null) {
        return;
    // The clipboard does not contain text. If it contains a URI, attempts to get data from it
    } else {
        Uri pasteUri = item.getUri();
        // If the URI contains something, try to get text from it
        if (pasteUri != null) {
            // calls a routine to resolve the URI and get data from it. This routine is not
            // presented here.
            pasteData = resolveUri(Uri);
            return;
        } else {
        // Something is wrong. The MIME type was plain text, but the clipboard does not contain either
        // text or a Uri. Report an error.
        Log.e("Clipboard contains an invalid data type");
        return;
        }
    }

URI:步骤类似
    ClipData clip = clipboard.getPrimaryClip();
    if (clip != null) {
        // Gets the first item from the clipboard data
        ClipData.Item item = clip.getItemAt(0);
        // Tries to get the item's contents as a URI
        Uri pasteUri = item.getUri();
        // If the clipboard contains a URI reference
        if (pasteUri != null) {
            // Is this a content URI?
            String uriMimeType = cr.getType(pasteUri);
            // If the return value is not null, the Uri is a content Uri
            if (uriMimeType != null) {
                // Does the content provider offer a MIME type that the current application can use?
                if (uriMimeType.equals(MIME_TYPE_CONTACT)) {
                    // Get the data from the content provider.
                    Cursor pasteCursor = cr.query(uri, null, null, null, null);
                    // If the Cursor contains data, move to the first record
                    if (pasteCursor != null) {
                        if (pasteCursor.moveToFirst()) {
                        // get the data from the Cursor here. The code will vary according to the
                        // format of the data model.
                        }
                    }
                    // close the Cursor
                    pasteCursor.close();
                 }
             }
         }
    }
Intent:
    // Checks to see if the clip item contains an Intent, by testing to see if getIntent() returns null
    Intent pasteIntent = clipboard.getPrimaryClip().getItemAt(0).getIntent();
    if (pasteIntent != null) {
        // handle the Intent
    } else {
        // ignore the clipboard, or issue an error if your application was expecting an Intent to be
        // on the clipboard
    }
一路看下来,好像是一路判断就行,就三种,得到的数据判断一下是否为空,就是这种数据